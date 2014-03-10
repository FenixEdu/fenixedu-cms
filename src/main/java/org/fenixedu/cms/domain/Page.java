package org.fenixedu.cms.domain;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

public class Page extends Page_Base {
    
    public Page() {
        super();
        this.setCreationDate(new DateTime());
        if(Authenticate.getUser() == null){
            throw new RuntimeException("Needs Login");
        }
        this.setCreatedBy(Authenticate.getUser());
    }
    
    @Override
    public void setName(LocalizedString name) {
        LocalizedString prevName = getName();
        super.setName(name);

        if (prevName == null) {
            setSlug(Site.slugify(name.getContent()));
        }
    }
    
    public Component componentForOid(String oid){
        for (Component c : getComponentsSet()){
            if (c.getExternalId().equals(oid)){
                return c;
            }
        }
        return null;
    }
    
    @Atomic
    public void delete(){
        for (Component component : getComponentsSet()) {
            component.delete();
        }
        this.setTemplate(null);
        this.setSite(null);
        this.setCreatedBy(null);
        this.deleteDomainObject();
    }

    public String getAddress() {
        String path = CoreConfiguration.getConfiguration().applicationUrl();
        if (path.charAt(path.length()-1) != '/') {
            path += "/";
        }
        path += getSite().getSlug() + "/" + getSlug();
        return path;
    }
}
