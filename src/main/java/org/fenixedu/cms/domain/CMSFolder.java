/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of FenixEdu CMS.
 *
 * FenixEdu CMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu CMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu CMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.cms.domain;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.portal.domain.MenuContainer;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.signals.DomainObjectEvent;
import org.fenixedu.bennu.signals.Signal;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.Atomic;

public class CMSFolder extends CMSFolder_Base {

    public static final String SIGNAL_CREATED = "fenixedu.cms.folder.created";
    public static final String SIGNAL_DELETED = "fenixedu.cms.folder.deleted";

    public CMSFolder(MenuContainer parent, String path, LocalizedString description) {
        super();
        setBennu(Bennu.getInstance());
        setFunctionality(new MenuFunctionality(parent, false, path, "cms", "anyone", description, description, path));
        Signal.emit(SIGNAL_CREATED,new DomainObjectEvent<>(this));
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
        Signal.emit(SIGNAL_DELETED, this.getOid());
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
