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

import com.google.common.base.Strings;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.I18N;
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
@RequestMapping("/cms/categories")
public class AdminCategory {
    @RequestMapping(value = "{slug}", method = RequestMethod.GET)
    public String categories(Model model, @PathVariable(value = "slug") String slug) {
        Site site = Site.fromSlug(slug);

        AdminSites.canEdit(site);

        model.addAttribute("site", site);
        model.addAttribute("categories", site.getCategoriesSet());
        return "fenixedu-cms/categories";
    }

    @RequestMapping(value = "{slug}/create", method = RequestMethod.GET)
    public String createCategory(Model model, @PathVariable(value = "slug") String slug) {
        Site s = Site.fromSlug(slug);

        AdminSites.canEdit(s);

        model.addAttribute("site", s);
        return "fenixedu-cms/createCategory";
    }

    @RequestMapping(value = "{slug}/create", method = RequestMethod.POST)
    public RedirectView createCategory(Model model, @PathVariable(value = "slug") String slug, @RequestParam String name,
                                       RedirectAttributes redirectAttributes) {
        if (Strings.isNullOrEmpty(name)) {
            redirectAttributes.addFlashAttribute("emptyName", true);
            return new RedirectView("/cms/categories/" + slug + "/create", true);
        }
        Site s = Site.fromSlug(slug);

        AdminSites.canEdit(s);

        createCategory(s, name);
        return new RedirectView("/cms/categories/" + s.getSlug() + "", true);
    }

    @Atomic
    private void createCategory(Site site, String name) {
        Category p = new Category(site);
        p.setName(new LocalizedString(I18N.getLocale(), name));
    }

    @RequestMapping(value = "{slugSite}/{slugCategories}/delete", method = RequestMethod.POST)
    public RedirectView delete(Model model, @PathVariable(value = "slugSite") String slugSite, @PathVariable(
            value = "slugCategories") String slugCategories) {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        s.categoryForSlug(slugCategories).delete();
        return new RedirectView("/cms/categories/" + s.getSlug() + "", true);
    }

    @RequestMapping(value = "{slugSite}/{slugCategories}", method = RequestMethod.GET)
    public RedirectView viewCategory(@PathVariable(value = "slugSite") String slugSite,
                                     @PathVariable(value = "slugCategories") String slugCategories) {
        return new RedirectView("/cms/posts/" + slugSite + "?category=" + slugCategories, true);
    }


}
