package org.fenixedu.bennu.cms.domain;

public class CMSTemplate extends CMSTemplate_Base {
    
    public CMSTemplate() {
        super();
    }

    public void delete() {
        for (Page page : getPagesSet()) {
            page.setTemplate(null);
        }
        this.setFile(null);
        this.setTheme(null);
        this.deleteDomainObject();
    }
    
}
