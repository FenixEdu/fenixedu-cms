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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.cms.domain.CMSTheme;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

@SpringApplication(group = "logged", path = "cms", title = "application.title.cms")
@SpringFunctionality(app = AdminSites.class, title = "application.admin-portal.title")
@RequestMapping("/cms/sites")
public class AdminSites {

    private static final int ITEMS_PER_PAGE = 30;

    @RequestMapping
    public String list(HttpServletRequest request, Model model) {
        return list(0, model);
    }

    @RequestMapping("/{slug}")
    public String manage(Model model, @PathVariable String slug) {
        Site site = Site.fromSlug(slug);
        canEdit(site);
        model.addAttribute("site", site);
        return "fenixedu-cms/manageSite";
    }

    @RequestMapping(value = "manage/{page}", method = RequestMethod.GET)
    public String list(@PathVariable(value = "page") int page, Model model) {
        List<List<Site>> pages = Lists.partition(getSites(), ITEMS_PER_PAGE);
        int currentPage = normalize(page, pages);
        model.addAttribute("numberOfPages", pages.size());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("sites", pages.isEmpty() ? Collections.emptyList() : pages.get(currentPage));
        model.addAttribute("isManager", DynamicGroup.get("managers").isMember(Authenticate.getUser()));
        return "fenixedu-cms/manage";
    }

    private int normalize(int page, List<List<Site>> pages) {
        if (page < 0) {
            return 0;
        }
        if (page >= pages.size()) {
            return pages.size() - 1;
        }
        return page;
    }

    private List<Site> getSites() {
        User user = Authenticate.getUser();
        Set<Site> allSites = Bennu.getInstance().getSitesSet();
        Predicate<Site> isAdminMember = site -> site.getCanAdminGroup().isMember(user);
        Predicate<Site> isPostsMember = site -> site.getCanPostGroup().isMember(user);
        return allSites.stream().filter(isAdminMember.or(isPostsMember)).collect(Collectors.toList());
    }

    public static void canEdit(Site site) {
        if (site == null) {
            throw CmsDomainException.notFound();
        }
        if (!(site.getCanAdminGroup().isMember(Authenticate.getUser()))) {
            throw CmsDomainException.forbiden();
        }
    }

    @RequestMapping(value = "{slug}/edit", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable(value = "slug") String slug) {
        Site site = Site.fromSlug(slug);

        AdminSites.canEdit(site);

        model.addAttribute("site", site);
        model.addAttribute("themes", Bennu.getInstance().getCMSThemesSet());
        model.addAttribute("folders", Bennu.getInstance().getCmsFolderSet());
        return "fenixedu-cms/editSite";
    }

    @RequestMapping(value = "{slug}/edit", method = RequestMethod.POST)
    public RedirectView edit(Model model, @PathVariable(value = "slug") String slug, @RequestParam LocalizedString name,
            @RequestParam LocalizedString description, @RequestParam String theme, @RequestParam String newSlug, @RequestParam(
            required = false) Boolean published, RedirectAttributes redirectAttributes, @RequestParam String viewGroup,
            @RequestParam String postGroup, @RequestParam String adminGroup, @RequestParam String folder,
            @RequestParam String analyticsCode) {

        if (name.isEmpty()) {
            redirectAttributes.addFlashAttribute("emptyName", true);
            return new RedirectView("/sites/" + slug + "/edit", true);
        } else {
            if (published == null) {
                published = false;
            }
            Site s = Site.fromSlug(slug);

            AdminSites.canEdit(s);

            editSite(name, description, theme, newSlug, published, s, viewGroup, postGroup, adminGroup, folder, analyticsCode);
            return new RedirectView("/cms/sites/" + newSlug, true);
        }
    }

    @Atomic(mode = TxMode.WRITE)
    private void editSite(LocalizedString name, LocalizedString description, String theme, String slug, Boolean published,
            Site s, String viewGroup, String postGroup, String adminGroup, String folder, String analyticsCode) {
        s.setName(name);
        s.setDescription(description);
        s.setTheme(CMSTheme.forType(theme));
        if (!Strings.isNullOrEmpty(folder)) {
            s.setFolder(FenixFramework.getDomainObject(folder));
        } else if (s.getFolder() != null) {
            // Remove the folder and set the new slug, so the MenuFunctionality will be created
            s.setFolder(null);
            s.setSlug(slug);
            s.updateMenuFunctionality();
        }

        if (!s.getSlug().equals(slug)) {
            s.setSlug(slug);
            s.updateMenuFunctionality();
        }

        s.setAnalyticsCode(analyticsCode);

        s.setPublished(published);
        s.setCanViewGroup(Group.parse(viewGroup));
        s.setCanPostGroup(Group.parse(postGroup));
        s.setCanAdminGroup(Group.parse(adminGroup));
    }

    @RequestMapping(value = "{slug}/delete", method = RequestMethod.POST)
    public RedirectView delete(@PathVariable(value = "slug") String slug) {
        Site s = Site.fromSlug(slug);

        AdminSites.canEdit(s);

        s.delete();
        return new RedirectView("/cms/sites", true);
    }

    @RequestMapping(value = "default", method = RequestMethod.POST)
    public RedirectView setAsDefault(@RequestParam String slug) {
        Site s = Site.fromSlug(slug);

        if (!DynamicGroup.get("managers").isMember(Authenticate.getUser())) {
            throw CmsDomainException.forbiden();
        }

        makeDefaultSite(s);

        return new RedirectView("/cms/sites", true);
    }

    @Atomic
    private void makeDefaultSite(Site s) {
        Bennu.getInstance().setDefaultSite(s);
    }

}