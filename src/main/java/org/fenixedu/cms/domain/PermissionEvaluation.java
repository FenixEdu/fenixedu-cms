/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of FenixEdu CMS.
 *
 * FenixEdu CMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu CMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu CMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.cms.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.domain.PermissionsArray.Permission;
import org.fenixedu.cms.exceptions.CmsDomainException;

import com.google.common.collect.Sets;

/**
 * Created by borgez-dsi on 08-09-2015.
 */
public class PermissionEvaluation {

    public static boolean canAccess(User user, Site site) {
        return Group.parse("#managers").isMember(user)
                || site.getRolesSet().stream().filter(x -> x.getGroup().isMember(user)).findAny().isPresent();
    }

    public static boolean canDoThis(User user, Site site, Permission... permissions) {
        HashSet<Permission> requiredPerms = Sets.newHashSet(permissions);
        if (Group.parse("#managers").isMember(user)) {
            return true;
        }

        for (Role role : site.getRolesSet()) {
            Set<Permission> availablePerms = role.getRoleTemplate().getPermissions().get();
            Set<Permission> intersection = Sets.intersection(availablePerms, requiredPerms);
            if (!intersection.isEmpty() && role.getGroup().isMember(user)) {
                requiredPerms.removeAll(intersection);
            }
        }

        return requiredPerms.isEmpty();
    }

    public static boolean canDoThis(Site site, String permissions) {
        return canDoThis(site,
                Stream.of(permissions.split(",")).map(String::trim).map(Permission::valueOf).toArray(Permission[]::new));
    }

    public static boolean canDoThis(Site site, Permission... permissions) {
        return canDoThis(Authenticate.getUser(), site, permissions);
    }

    public static void ensureCanDoThis(User user, Site site, Permission... permissions) {
        if (!canDoThis(user, site, permissions)) {
            throw CmsDomainException.forbiden();
        }
    }

    public static void ensureCanDoThis(Site site, Permission... permissions) {
        ensureCanDoThis(Authenticate.getUser(), site, permissions);
    }

}
