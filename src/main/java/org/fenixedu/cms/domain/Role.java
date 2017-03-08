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
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.signals.DomainObjectEvent;
import org.fenixedu.bennu.core.signals.Signal;
import org.fenixedu.commons.i18n.LocalizedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Role extends Role_Base {
    
    private static final Logger logger = LoggerFactory.getLogger(Role.class);
    
    public static final String SIGNAL_CREATED = "fenixedu.cms.role.created";
    public static final String SIGNAL_DELETED = "fenixedu.cms.role.deleted";
    public static final String SIGNAL_EDITED = "fenixedu.cms.role.edited";

    public Role(RoleTemplate template, Site site) {
        setRoleTemplate(template);
        setSite(site);
        setGroup(Group.nobody());
        Signal.emit(SIGNAL_CREATED,new DomainObjectEvent<>(this));
    }

    public void delete() {
        logger.info("Role " + getName().getContent() + " -  " + getExternalId() +
                " deleted by user " + Authenticate.getUser().getExternalId());
        Signal.emit(SIGNAL_DELETED, this.getOid());
        setRoleTemplate(null);
        setSite(null);
        setPersistentGroup(null);
        super.deleteDomainObject();
    }
    
    public void setGroup(Group group) {
        logger.info("Role " + getName().getContent() + " - " + getExternalId() +
                " changed to " + group.getExpression() + " by user "+ Authenticate.getUser().getExternalId());
        setPersistentGroup(group.toPersistentGroup());
    }
    
    public Group getGroup(){
        if(getPersistentGroup()==null){
            return Group.nobody();
        }
        return getPersistentGroup().toGroup();
    }
    
    public LocalizedString getName() {
        return getRoleTemplate() !=null ? getRoleTemplate().getName() : new LocalizedString();
    }
}
