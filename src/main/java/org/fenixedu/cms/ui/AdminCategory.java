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
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.PermissionEvaluation;
import org.fenixedu.cms.domain.PermissionsArray;
import org.fenixedu.cms.domain.Post;
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

    @RequestMapping(value = "{slugSite}/create", method = RequestMethod.POST)
    public RedirectView createCategory(@PathVariable String slugSite, @RequestParam LocalizedString name) {
        Site s = Site.fromSlug(slugSite);
        AdminSites.canEdit(s);
        Category c = createCategory(s, name);
        return new RedirectView("/cms/categories/" + s.getSlug() + "/" + c.getSlug(), true);
    }

    @RequestMapping(value = "{slugSite}/{slugCategory}/delete", method = RequestMethod.POST)
    public RedirectView delete(@PathVariable(value = "slugSite") String slugSite, @PathVariable String slugCategory) {
        Site s = Site.fromSlug(slugSite);
        FenixFramework.atomic(() -> {
            AdminSites.canEdit(s);
            s.categoryForSlug(slugCategory).delete();
        });
        return new RedirectView("/cms/categories/" + s.getSlug(), true);
    }

    @RequestMapping(value = "{slugSite}/{slugCategory}/createCategoryPost", method = RequestMethod.POST)
    public RedirectView  createCategoryPost(@PathVariable(value = "slugSite") String slugSite,
                                               @PathVariable String slugCategory, @RequestParam LocalizedString name) {
        Site s = Site.fromSlug(slugSite);
        AdminSites.canEdit(s);
        Category c = s.categoryForSlug(slugCategory);
        createPost(s, c, name);
        return new RedirectView("/cms/categories/" + s.getSlug() + "/" + c.getSlug(), true);
    }

    @RequestMapping(value = "{slugSite}/{slugCategory}", method = RequestMethod.GET)
    public String viewCategory(Model model, @PathVariable String slugSite, @PathVariable String slugCategory) {
        Site s = Site.fromSlug(slugSite);
        AdminSites.canEdit(s);
        model.addAttribute("site", s);
        model.addAttribute("category", s.categoryForSlug(slugCategory));
        return "fenixedu-cms/editCategory";
    }

    @RequestMapping(value = "{slugSite}/{slugCategory}", method = RequestMethod.POST)
    public RedirectView editCategory(@PathVariable String slugSite, @PathVariable String slugCategory,
                                     @RequestParam LocalizedString name) {
        Site s = Site.fromSlug(slugSite);
        AdminSites.canEdit(s);
        Category c = s.categoryForSlug(slugCategory);
        FenixFramework.atomic(()->{
            AdminSites.canEdit(s);
            c.setName(name);
        });
        return new RedirectView("/cms/categories/" + s.getSlug() + "/" + c.getSlug(), true);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    private Category createCategory(Site site, LocalizedString name) {
        return new Category(site, name);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    private Post createPost(Site site, Category category, LocalizedString name) {
        PermissionEvaluation.ensureCanDoThis(site, PermissionsArray.Permission.CREATE_POST);
        Post p = new Post(site);
        p.setName(Post.sanitize(name));
        p.setBody(new LocalizedString());
        p.setCanViewGroup(site.getCanViewGroup());
        p.setActive(false);
        p.addCategories(category);
        return p;
    }

}
