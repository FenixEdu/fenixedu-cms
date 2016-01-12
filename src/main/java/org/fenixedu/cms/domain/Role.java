package org.fenixedu.cms.domain;

import org.fenixedu.bennu.core.groups.NobodyGroup;

public class Role extends Role_Base {

    public Role(RoleTemplate template, Site site) {
        setName(template.getDescription());
        setRoleTemplate(template);
        setSite(site);
        setGroup(NobodyGroup.get().toPersistentGroup());
    }

    public void delete() {
        setRoleTemplate(null);
        setSite(null);
        setGroup(null);
        super.deleteDomainObject();
    }

    public static void rolesWithPermission(Site site, String permissionType) {

    }

}
