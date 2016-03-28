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

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.signals.DomainObjectEvent;
import org.fenixedu.bennu.core.signals.Signal;

public class Role extends Role_Base {

    public static final String SIGNAL_CREATED = "fenixedu.cms.role.created";
    public static final String SIGNAL_DELETED = "fenixedu.cms.role.deleted";
    public static final String SIGNAL_EDITED = "fenixedu.cms.role.edited";

    public Role(RoleTemplate template, Site site) {
        setName(template.getDescription());
        setRoleTemplate(template);
        setSite(site);
        setGroup(Group.nobody().toPersistentGroup());
        Signal.emit(SIGNAL_CREATED,new DomainObjectEvent<>(this));
    }

    public void delete() {
        Signal.emit(SIGNAL_DELETED, this.getOid());
        setRoleTemplate(null);
        setSite(null);
        setGroup(null);
        super.deleteDomainObject();
    }

    public static void rolesWithPermission(Site site, String permissionType) {

    }

}
