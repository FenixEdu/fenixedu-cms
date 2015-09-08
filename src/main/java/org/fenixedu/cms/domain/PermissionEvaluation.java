package org.fenixedu.cms.domain;

import com.google.common.collect.Sets;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.AuthorizationException;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.domain.PermissionsArray.Permission;
import org.fenixedu.cms.exceptions.CmsDomainException;

import java.util.HashSet;
import java.util.stream.Stream;

/**
 * Created by borgez-dsi on 08-09-2015.
 */
public class PermissionEvaluation {

    public static boolean canAccess(User user, Site site){
        return DynamicGroup.get("manager").isMember(user) || site.getRolesSet().stream().filter(x -> x.getGroup().isMember(user)).findAny().isPresent();
    }

    public static boolean canDoThis(User user, Site site, Permission... permissions) {
        HashSet<Permission> permissionsSet = Sets.newHashSet(permissions);
        return DynamicGroup.get("manager").isMember(user) || site.getRolesSet().stream()
                .filter(role -> !Sets.intersection(role.getRoleTemplate().getPermissions().get(), permissionsSet).isEmpty())
                .filter(role -> role.getGroup().isMember(user)).findAny().isPresent();
    }

    public static boolean canDoThis(Site site, String permissions) {
        return canDoThis(site, Stream.of(permissions.split(",")).map(Permission::valueOf).toArray(Permission[]::new));
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
