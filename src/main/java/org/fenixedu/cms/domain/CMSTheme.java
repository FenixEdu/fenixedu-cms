package org.fenixedu.cms.domain;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.security.Authenticate;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class CMSTheme extends CMSTheme_Base {

    public CMSTheme() {
        super();
        if (Authenticate.getUser() == null) {
            throw new RuntimeException("Needs Login");
        }
        this.setCreatedBy(Authenticate.getUser());
        this.setCreationDate(new DateTime());
    }

    public static CMSTheme forType(String t) {
        for (CMSTheme theme : Bennu.getInstance().getCMSThemesSet()) {
            if (theme.getType().equals(t)) {
                return theme;
            }
        }
        return null;
    }
    
    public CMSTemplate templateForType(String t) {
        for (CMSTemplate template : this.getTemplatesSet()) {
            if (template.getType().equals(t)) {
                return template;
            }
        }
        return null;
    }

    public CMSTemplateFile fileForPath(String t) {
        for (CMSTemplateFile file : this.getFilesSet()) {
            if (file.getDisplayName().equals(t)) {
                return file;
            }
        }
        return null;
    }
    
    public boolean isDefault(){
        return Bennu.getInstance().getDefaultCMSTheme() == this;
    }
    
    @Atomic(mode=TxMode.WRITE)
    public void delete() {
        if (this.getChildrenSet().size() != 0) {
            throw new RuntimeException("Themes depend of this theme. Can't delete");
        }
        
        for(Site site : getSitesSet()){
            site.setTheme(null);
        }
        this.setPrimaryBennu(null);
        this.setBennu(null);
        
        if (Bennu.getInstance().getCMSThemesSet().size() == 0) {
            Bennu.getInstance().setDefaultCMSTheme(null);
        }else{
            Bennu.getInstance().setDefaultCMSTheme(Bennu.getInstance().getCMSThemesSet().iterator().next());
        }
        
        this.setCreatedBy(null);
        this.setExtended(null);
        
        for (CMSTemplate template : this.getTemplatesSet()) {
            template.delete();
        }
        
        for (CMSTemplateFile file : this.getFilesSet()) {
            file.setTemplate(null);
            file.setTheme(null);
            file.delete();
        }
        
        this.deleteDomainObject();

    }
}
