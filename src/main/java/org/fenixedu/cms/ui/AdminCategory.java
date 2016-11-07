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

import static org.fenixedu.cms.domain.PermissionEvaluation.canDoThis;
import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.signals.DomainObjectEvent;
import org.fenixedu.bennu.signals.Signal;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.PermissionsArray.Permission;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.SiteActivity;
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
        ensureCanDoThis(site, Permission.LIST_CATEGORIES);
        model.addAttribute("site", site);
        model.addAttribute("categories", getCategories(site));
        return "fenixedu-cms/categories";
    }

    @RequestMapping(value = "{slugSite}/create", method = RequestMethod.POST)
    public RedirectView createCategory(@PathVariable String slugSite, @RequestParam LocalizedString name) {
        Site s = Site.fromSlug(slugSite);
        Category c = createCategory(s, name);
        return new RedirectView("/cms/categories/" + s.getSlug() + "/" + c.getSlug(), true);
    }

    @RequestMapping(value = "{slugSite}/{slugCategory}/delete", method = RequestMethod.POST)
    public RedirectView delete(@PathVariable String slugSite, @PathVariable String slugCategory) {
        FenixFramework.atomic(() -> {
            Site s = Site.fromSlug(slugSite);
            ensureCanDoThis(s, Permission.LIST_CATEGORIES, Permission.EDIT_CATEGORY, Permission.DELETE_CATEGORY);

            Category category = s.categoryForSlug(slugCategory);
            if(category.getPrivileged()) {
                ensureCanDoThis(s, Permission.EDIT_PRIVILEGED_CATEGORY);
            }

            category.delete();
        });
        return new RedirectView("/cms/categories/" + slugSite, true);
    }

    @RequestMapping(value = "{slugSite}/{slugCategory}/createCategoryPost", method = RequestMethod.POST)
    public RedirectView  createCategoryPost(@PathVariable String slugSite,
                                            @PathVariable String slugCategory,
                                            @RequestParam LocalizedString name) {
        Site s = Site.fromSlug(slugSite);
        Category c = s.categoryForSlug(slugCategory);
        createPost(s, c, name);
        return new RedirectView("/cms/categories/" + s.getSlug() + "/" + c.getSlug(), true);
    }

    @RequestMapping(value = "{slugSite}/{slugCategory}", method = RequestMethod.GET)
    public String viewCategory(Model model, @PathVariable String slugSite, @PathVariable String slugCategory) {
        Site s = Site.fromSlug(slugSite);
        ensureCanDoThis(s, Permission.LIST_CATEGORIES, Permission.EDIT_CATEGORY);
        Category category = s.categoryForSlug(slugCategory);
        if(category.getPrivileged()) {
            ensureCanDoThis(s, Permission.USE_PRIVILEGED_CATEGORY);
        }
        model.addAttribute("site", s);
        model.addAttribute("category", category);
        return "fenixedu-cms/editCategory";
    }

    @RequestMapping(value = "{slugSite}/{slugCategory}", method = RequestMethod.POST)
    public RedirectView editCategory(@PathVariable String slugSite, @PathVariable String slugCategory,
                                     @RequestParam LocalizedString name,
                                     @RequestParam(required = false, defaultValue = "false") boolean privileged) {
        Site s = Site.fromSlug(slugSite);
        Category c = s.categoryForSlug(slugCategory);
        FenixFramework.atomic(()->{
            ensureCanDoThis(s, Permission.LIST_CATEGORIES, Permission.EDIT_CATEGORY);
            if(c.getPrivileged()) {
                ensureCanDoThis(s, Permission.USE_PRIVILEGED_CATEGORY, Permission.EDIT_PRIVILEGED_CATEGORY);
            }
            c.setPrivileged(privileged);
            c.setName(name);
        });
        Signal.emit(Category.SIGNAL_EDITED, new DomainObjectEvent<>(c));
        return new RedirectView("/cms/categories/" + s.getSlug() + "/" + c.getSlug(), true);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    private Category createCategory(Site site, LocalizedString name) {
        ensureCanDoThis(site, Permission.LIST_CATEGORIES, Permission.EDIT_CATEGORY, Permission.CREATE_CATEGORY);
        return new Category(site, name);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    private Post createPost(Site site, Category category, LocalizedString name) {
        ensureCanDoThis(site, Permission.LIST_CATEGORIES, Permission.EDIT_CATEGORY, Permission.CREATE_POST);
        if(category.getPrivileged()) {
            ensureCanDoThis(site, Permission.USE_PRIVILEGED_CATEGORY);
        }
        Post p = new Post(site);
        p.setName(Post.sanitize(name));
        p.setBodyAndExcerpt(new LocalizedString(), new LocalizedString());
        p.setCanViewGroup(site.getCanViewGroup());
        p.setActive(false);
        p.addCategories(category);
        SiteActivity.createdPost(p, Authenticate.getUser());
        return p;
    }

    public List<Category> getCategories(Site site) {
        boolean canUsePrivileged = canDoThis(site, Permission.USE_PRIVILEGED_CATEGORY);
        return site.getCategoriesSet().stream()
            .filter(category->!category.getPrivileged() || canUsePrivileged)
            .sorted(Category.CATEGORY_NAME_COMPARATOR).collect(Collectors.toList());
    }
}
