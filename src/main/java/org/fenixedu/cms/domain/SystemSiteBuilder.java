package org.fenixedu.cms.domain;

import org.fenixedu.commons.i18n.LocalizedString;
import pt.ist.fenixframework.consistencyPredicates.ConsistencyPredicate;

public class SystemSiteBuilder extends SystemSiteBuilder_Base {
    
    public SystemSiteBuilder() {
        super();
    }
    
    
    @Override
    public Site create(LocalizedString name, LocalizedString description) {
        Site site = super.create(name, description);
        site.setDefaultRoleTemplate(this.getDefaultRoleTemplate());
        return site;
    }
    
    @Override
    public boolean isSystemBuilder(){return true;}
    
    @Override
    public void setDefaultRoleTemplate(RoleTemplate defaultRoleTemplate) {
        super.setDefaultRoleTemplate(defaultRoleTemplate);
        addRoleTemplate(defaultRoleTemplate);
    }
    
    @ConsistencyPredicate
    private boolean checkDefaultOnRoles() {
        return getDefaultRoleTemplate() == null || getRoleTemplateSet().contains(getDefaultRoleTemplate());
    }
}
