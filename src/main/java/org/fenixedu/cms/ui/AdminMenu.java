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
import org.springframework.web.servlet.view.RedirectView;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import java.util.Comparator;

import static java.util.stream.Collectors.toList;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/menus")
public class AdminMenu {

    @RequestMapping(value = "{siteSlug}", method = RequestMethod.GET)
    public String menus(Model model, @PathVariable String siteSlug) {
        Site site = Site.fromSlug(siteSlug);
        AdminSites.canEdit(site);
        model.addAttribute("site", site);
        model.addAttribute("menus", site.getMenusSet().stream().sorted(Comparator.comparing(Menu::getName)).collect(toList()));
        return "fenixedu-cms/menus";
    }

    @RequestMapping(value = "{siteSlug}/create", method = RequestMethod.POST)
    public RedirectView createMenu(@PathVariable(value = "siteSlug") String slug, @RequestParam LocalizedString name) {
        Site s = Site.fromSlug(slug);
        Menu menu = createMenu(s, name);
        return new RedirectView("/cms/menus/" + s.getSlug() + "/" + menu.getSlug() + "/change", true);
    }

    @RequestMapping(value = "{slugSite}/{slugMenu}/delete", method = RequestMethod.POST)
    public RedirectView delete(@PathVariable String slugSite, @PathVariable String slugMenu) {
        Site s = Site.fromSlug(slugSite);
        FenixFramework.atomic(() -> {
            AdminSites.canEdit(s);
            s.menuForSlug(slugMenu).delete();
        });
        return new RedirectView("/cms/menus/" + s.getSlug(), true);
    }

    @Atomic
    private Menu createMenu(Site site, LocalizedString name) {
        AdminSites.canEdit(site);
        return new Menu(site, name);
    }

}
