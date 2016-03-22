package org.fenixedu.cms.domain;

import org.fenixedu.bennu.core.groups.NobodyGroup;
import org.fenixedu.bennu.signals.DomainObjectEvent;
import org.fenixedu.bennu.signals.Signal;

public class Role extends Role_Base {

    public static final String SIGNAL_CREATED = "fenixedu.cms.role.created";
    public static final String SIGNAL_DELETED = "fenixedu.cms.role.deleted";
    public static final String SIGNAL_EDITED = "fenixedu.cms.role.edited";

    public Role(RoleTemplate template, Site site) {
        setName(template.getDescription());
        setRoleTemplate(template);
        setSite(site);
        setGroup(NobodyGroup.get().toPersistentGroup());
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
