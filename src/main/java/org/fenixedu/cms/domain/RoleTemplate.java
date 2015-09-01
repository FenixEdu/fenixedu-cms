package org.fenixedu.cms.domain;

import org.fenixedu.bennu.core.domain.Bennu;

public class RoleTemplate extends RoleTemplate_Base {

    public RoleTemplate() {
        setBennu(Bennu.getInstance());
    }

    public void delete() {
        setBennu(null);
        getRolesSet().stream().forEach(Role::delete);
        super.deleteDomainObject();
    }
    
    public long getNumSites() {
        return getRolesSet().stream().map(Role::getSite).count();
    }

}
