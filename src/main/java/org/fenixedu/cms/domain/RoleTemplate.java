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
