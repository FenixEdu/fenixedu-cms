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

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.CMSTemplate;
import org.fenixedu.cms.domain.CMSTheme;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.PermissionEvaluation;
import org.fenixedu.cms.domain.PermissionsArray.Permission;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.SiteActivity;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collection;
import java.util.Objects;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

import static java.util.Optional.ofNullable;
import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;
import static org.fenixedu.cms.ui.SearchUtils.searchPages;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/pages/advanced")
public class AdminPagesAdvanced {
    private static final int PER_PAGE = 10;

    @RequestMapping(value = "{slug}", method = RequestMethod.GET)
    public String pages(Model model, @PathVariable String slug,
                        @RequestParam(required = false) String query,
                        @RequestParam(required = false, defaultValue = "1") int currentPage) {
        Site site = Site.fromSlug(slug);
        ensureCanDoThis(site, Permission.SEE_PAGES);
        AdminSites.canEdit(site);
        Collection<Page> allPages = Strings.isNullOrEmpty(query) ? site.getPagesSet() : searchPages(site.getPagesSet(), query);
        SearchUtils.Partition<Page> partition =
                        new SearchUtils.Partition<>(allPages, Page.CREATION_DATE_COMPARATOR, PER_PAGE, currentPage);

        model.addAttribute("site", site);
        model.addAttribute("query", query);
        model.addAttribute("partition", partition);
        model.addAttribute("pages", partition.getItems());
        return "fenixedu-cms/pagesAdvanced";
    }

    @RequestMapping(value = "{slug}/create", method = RequestMethod.POST)
    public RedirectView createPage(@PathVariable String slug, @RequestParam LocalizedString name) {
        Site s = Site.fromSlug(slug);
        AdminSites.canEdit(s);
        Page page = createPage(name, s);
        return new RedirectView("/cms/pages/advanced/" + s.getSlug() + "/" + page.getSlug() + "/edit", true);
    }

    @Atomic
    private Page createPage(LocalizedString name, Site s) {
        ensureCanDoThis(s, Permission.SEE_PAGES, Permission.EDIT_PAGE, Permission.CREATE_PAGE);
        Page p = new Page(s, name);
        SiteActivity.createdPage(p, Authenticate.getUser());
        return p;
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/edit", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable String slugSite, @PathVariable String slugPage) {
        Site s = Site.fromSlug(slugSite);
        ensureCanDoThis(s, Permission.SEE_PAGES, Permission.EDIT_PAGE);
        AdminSites.canEdit(s);

        if (slugPage.equals("--**--")) {
            slugPage = "";
        }

        Page p = s.pageForSlug(slugPage);
        model.addAttribute("site", s);
        model.addAttribute("page", p);
        if (p.isStaticPage()) {
            model.addAttribute("post", p.getStaticPost());
        }
        model.addAttribute("availableComponents", Component.availableComponents(s));

        return "fenixedu-cms/editPageAdvanced";
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/edit", method = RequestMethod.POST)
    public RedirectView edit(@PathVariable String slugSite, @PathVariable String slugPage,
                             @RequestParam LocalizedString name, @RequestParam String template,
                             @RequestParam(required = false) String slug,
                             @RequestParam String viewGroup,
                             @RequestParam(required = false) Boolean published) {
        Site s = Site.fromSlug(slugSite);
        AdminSites.canEdit(s);
        ensureCanDoThis(s, Permission.SEE_PAGES, Permission.EDIT_PAGE);
        Page p = s.pageForSlug(slugPage.equals("--**--") ? "" : slugPage);
        slug = ofNullable(slug).orElseGet(()->p.getSlug());
        published = ofNullable(published).orElse(false);
        editPage(name, slug, template, s, p, ofNullable(published)
                .orElse(false), Group.parse(viewGroup));
        return new RedirectView("/cms/pages/advanced/" + slugSite + "/" + p.getSlug() + "/edit", true);
    }

    @Atomic(mode = TxMode.WRITE)
    private void editPage(LocalizedString name, String slug, String template, Site s, Page p, boolean published, Group canView) {
        p.setName(name);
        if (!Objects.equals(slug, p.getSlug())) {
            ensureCanDoThis(s, Permission.CHANGE_PATH_PAGES);
            p.setSlug(slug);
        }
        CMSTheme theme = s.getTheme();
        if (s != null && s.getTheme() != null && theme != null) {
            CMSTemplate t = theme.templateForType(template);
            p.setTemplate(t);
        }
        if (p.getPublished() != published) {
            PermissionEvaluation.canDoThis(s, Permission.PUBLISH_PAGES);
            p.setPublished(published);
        }
        if (!p.getCanViewGroup().equals(canView)) {
            PermissionEvaluation.canDoThis(s, Permission.PUBLISH_PAGES);
            p.setCanViewGroup(canView);
        }
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/delete", method = RequestMethod.POST)
    public RedirectView delete(@PathVariable String slugSite, @PathVariable String slugPage) {
        FenixFramework.atomic(() -> {
            Site site = Site.fromSlug(slugSite);
            AdminSites.canEdit(site);
            ensureCanDoThis(site, Permission.SEE_PAGES, Permission.EDIT_PAGE, Permission.DELETE_PAGE);
            site.pageForSlug(slugPage).delete();
        });
        return new RedirectView("/cms/pages/advanced/" + slugSite + "", true);
    }


}
