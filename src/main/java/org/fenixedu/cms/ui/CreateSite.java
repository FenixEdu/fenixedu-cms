/**
 * Copyright © 2014 Instituto Superior Técnico
 * <p/>
 * This file is part of FenixEdu CMS.
 * <p/>
 * FenixEdu CMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * FenixEdu CMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu CMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.cms.ui;

import com.google.common.base.Strings;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.CMSFolder;
import org.fenixedu.cms.domain.CMSTheme;
import org.fenixedu.cms.domain.CmsSettings;
import org.fenixedu.cms.domain.DefaultRoles;
import org.fenixedu.cms.domain.Role;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.SiteActivity;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import static java.util.Optional.ofNullable;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/sites/new")
public class CreateSite {

    @RequestMapping(method = RequestMethod.POST)
    public RedirectView create(@RequestParam LocalizedString name,
                               @RequestParam(required = false, defaultValue = "{}") LocalizedString description,
                               @RequestParam(required = false) String template,
                               @RequestParam(required = false) String theme,
                               @RequestParam(required = false, defaultValue = "false") boolean embedded,
                               @RequestParam(required = false) String folder,
                               RedirectAttributes redirectAttributes) {
       if (name.isEmpty()) {
            redirectAttributes.addFlashAttribute("emptyName", true);
            return new RedirectView("/cms/sites/new", true);
        } else {
            createSite(name, description, false, template, folder, embedded, theme);
            return new RedirectView("/cms/sites/", true);
        }
    }

    @Atomic
    private void createSite(LocalizedString name, LocalizedString description, boolean published,
                            String template, String folder, boolean embedded, String themeType) {
        CmsSettings.getInstance().ensureCanManageSettings();
        Site site = new Site(name,description);
        
        ofNullable(folder).filter(t -> !Strings.isNullOrEmpty(t)).map(FenixFramework::getDomainObject)
            .map(CMSFolder.class::cast).ifPresent(site::setFolder);

        site.setEmbedded(ofNullable(embedded).orElse(false));
        site.updateMenuFunctionality();
        site.setPublished(published);

        Role adminRole = new Role(DefaultRoles.getInstance().getAdminRole(), site);
        if(!Group.managers().isMember(Authenticate.getUser())) {
          adminRole.setGroup(Group.users(Authenticate.getUser()).toPersistentGroup());
        }
        new Role(DefaultRoles.getInstance().getAuthorRole(), site);
        new Role(DefaultRoles.getInstance().getContributorRole(), site);
        new Role(DefaultRoles.getInstance().getEditorRole(), site);

        ofNullable(template).filter(t->!Strings.isNullOrEmpty(t)).map(Site::templateFor).ifPresent(t -> t.makeIt(site));
        ofNullable(themeType).filter(t -> !Strings.isNullOrEmpty(t)).map(CMSTheme::forType).ifPresent(site::setTheme);

        SiteActivity.createdSite(site, Authenticate.getUser());
    }

}
