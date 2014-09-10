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
        if (getResolver() != null) {
            return getResolver().getStrategy().resolveSite(this, url);
        }
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

    public String getBaseUrl(Site site) {
        if (getResolver() != null) {
            return getResolver().getStrategy().getBaseUrl(this, site);
        }
        return getFunctionality().getFullPath().substring(1) + "/" + site.getSlug();
    }

    public static interface FolderResolver {

        public Site resolveSite(CMSFolder folder, String url);

        public String getBaseUrl(CMSFolder folder, Site site);

    }
}
