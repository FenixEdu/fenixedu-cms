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
package org.fenixedu.cms.ui;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/menus")
public class AdminMenu {
    @RequestMapping(value = "{slug}", method = RequestMethod.GET)
    public String posts(Model model, @PathVariable(value = "slug") String slug) {
        Site site = Site.fromSlug(slug);

        AdminSites.canEdit(site);

        model.addAttribute("site", site);
        model.addAttribute("menus", site.getMenusSet());
        return "fenixedu-cms/menus";
    }

    @RequestMapping(value = "{slug}/create", method = RequestMethod.GET)
    public String createMenu(Model model, @PathVariable(value = "slug") String slug) {
        Site s = Site.fromSlug(slug);

        AdminSites.canEdit(s);

        model.addAttribute("site", s);
        return "fenixedu-cms/createMenu";
    }

    @RequestMapping(value = "{slug}/create", method = RequestMethod.POST)
    public RedirectView createMenu(Model model, @PathVariable(value = "slug") String slug, @RequestParam LocalizedString name,
            RedirectAttributes redirectAttributes) {
        if (name.isEmpty()) {
            redirectAttributes.addFlashAttribute("emptyName", true);
            return new RedirectView("/cms/menus/" + slug + "/create", true);
        } else {
            Site s = Site.fromSlug(slug);
            createMenu(s, name);
            return new RedirectView("/cms/menus/" + s.getSlug() + "", true);
        }
    }

    @Atomic
    private void createMenu(Site site, LocalizedString name) {
        new Menu(site, name);
    }

    @RequestMapping(value = "{slugSite}/{oidMenu}/delete", method = RequestMethod.POST)
    public RedirectView delete(Model model, @PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "oidMenu") String oidMenu) {
        Site s = Site.fromSlug(slugSite);
        s.menuForOid(oidMenu).delete();
        return new RedirectView("/cms/menus/" + s.getSlug() + "", true);
    }
}
