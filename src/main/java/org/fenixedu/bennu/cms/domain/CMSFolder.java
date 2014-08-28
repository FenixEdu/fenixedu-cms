package org.fenixedu.bennu.cms.domain;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.portal.domain.MenuContainer;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.Atomic;

public class CMSFolder extends CMSFolder_Base {

    public CMSFolder(MenuContainer parent, String path, LocalizedString description) {
        super();
        setBennu(Bennu.getInstance());
        setFunctionality(new MenuFunctionality(parent, false, path, "cms", "anyone", description, description, path));
    }

    public Site resolveSite(String url) {
        url = url.substring(getFunctionality().getFullPath().length());
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        String[] parts = url.split("/");
        return getSiteSet().stream().filter(site -> parts[0].equals(site.getSlug())).findAny().orElse(null);
    }

    @Atomic
    public void delete() {
        MenuFunctionality functionality = getFunctionality();
        setFunctionality(null);
        functionality.delete();
        setBennu(null);
        deleteDomainObject();
    }

    @Override
    public MenuFunctionality getFunctionality() {
        return super.getFunctionality();
    }
}
