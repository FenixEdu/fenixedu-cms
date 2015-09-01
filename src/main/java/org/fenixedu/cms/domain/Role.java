package org.fenixedu.cms.domain;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.commons.i18n.LocalizedString;

public class Role extends Role_Base {
    
    public Role(LocalizedString name, RoleTemplate template, Site site) {
        setName(name);
        setRoleTemplate(template);
        setSite(site);
        setGroup(Group.nobody().toPersistentGroup());
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
