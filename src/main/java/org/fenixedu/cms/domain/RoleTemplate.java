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

import org.fenixedu.bennu.core.domain.Bennu;

import java.util.EnumSet;

public class RoleTemplate extends RoleTemplate_Base {

    public RoleTemplate() {
        setBennu(Bennu.getInstance());
        setPermissions(new PermissionsArray(EnumSet.noneOf(PermissionsArray.Permission.class)));
    }

    public void delete() {
        setBennu(null);
        getRolesSet().stream().forEach(Role::delete);
        setPermissions(null);
        super.deleteDomainObject();
    }
    
    public long getNumSites() {
        return getRolesSet().stream().map(Role::getSite).count();
    }

}
