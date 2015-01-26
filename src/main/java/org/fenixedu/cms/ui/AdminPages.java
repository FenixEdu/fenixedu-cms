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

import java.util.Objects;
import java.util.Optional;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.CMSTemplate;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.Component;
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
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.base.Strings;

import static java.util.Optional.ofNullable;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/pages")
public class AdminPages {

    @RequestMapping(value = "{slug}", method = RequestMethod.GET)
    public String pages(Model model, @PathVariable(value = "slug") String slug) {
        Site site = Site.fromSlug(slug);

        AdminSites.canEdit(site);

        model.addAttribute("site", site);
        model.addAttribute("pages", site.getPagesSet());
        return "fenixedu-cms/pages";
    }

    @RequestMapping(value = "{slug}/create", method = RequestMethod.GET)
    public String createPage(Model model, @PathVariable(value = "slug") String slug) {
        Site s = Site.fromSlug(slug);

        AdminSites.canEdit(s);

        model.addAttribute("site", s);
        return "fenixedu-cms/createPage";
    }

    @RequestMapping(value = "{slug}/create", method = RequestMethod.POST)
    public RedirectView createPage(Model model, @PathVariable(value = "slug") String slug, @RequestParam String name,
            RedirectAttributes redirectAttributes) {
        if (Strings.isNullOrEmpty(name)) {
            redirectAttributes.addFlashAttribute("emptyName", true);
            return new RedirectView("/cms/pages/" + slug + "/create", true);
        } else {
            Site s = Site.fromSlug(slug);

            AdminSites.canEdit(s);

            Page page = createPage(name, s);
            return new RedirectView("/cms/pages/" + s.getSlug() + "/" + page.getSlug() + "/edit", true);
        }
    }

    @Atomic
    private Page createPage(String name, Site s) {
        Page p = new Page(s);
        p.setName(new LocalizedString(I18N.getLocale(), name));
        return p;
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/edit", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "slugPage") String slugPage) {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        if (slugPage.equals("--**--")) {
            slugPage = "";
        }

        Page p = s.pageForSlug(slugPage);
        model.addAttribute("site", s);
        model.addAttribute("page", p);
        model.addAttribute("availableComponents", Component.availableComponents(s));

        return "fenixedu-cms/editPage";
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/edit", method = RequestMethod.POST)
    public RedirectView edit(Model model, @PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "slugPage") String slugPage, @RequestParam LocalizedString name, @RequestParam String slug,
            @RequestParam String template, @RequestParam(required = false) Boolean published, @RequestParam String viewGroup,
            RedirectAttributes redirectAttributes) {
        if (name != null && name.isEmpty()) {
            redirectAttributes.addFlashAttribute("emptyName", true);
            return new RedirectView("/cms/pages/" + slugSite + "/" + slugPage + "/edit", true);
        }
        Site s = Site.fromSlug(slugSite);
        AdminSites.canEdit(s);
        Page p = s.pageForSlug(slugPage.equals("--**--") ? "" : slugPage);
        editPage(name, slug, template, s, p, ofNullable(published).orElse(false), Group.parse(viewGroup));
        return new RedirectView("/cms/pages/" + slugSite + "/" + slugPage + "/edit", true);
    }

    @Atomic(mode = TxMode.WRITE)
    private void editPage(LocalizedString name, String slug, String template, Site s, Page p, boolean published, Group canView) {
        p.setName(name);
        if (!Objects.equals(slug, p.getSlug())) {
            p.setSlug(slug);
        }
        if (s != null && s.getTheme() != null) {
            CMSTemplate t = s.getTheme().templateForType(template);
            p.setTemplate(t);
        }
        if(p.getPublished() != published) {
            p.setPublished(published);
        }
        if(!p.getCanViewGroup().equals(canView)) {
            p.setCanViewGroup(canView);
        }
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/delete", method = RequestMethod.POST)
    public RedirectView delete(Model model, @PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "slugPage") String slugPage) {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        s.pageForSlug(slugPage).delete();
        return new RedirectView("/cms/pages/" + s.getSlug() + "", true);
    }

    @RequestMapping(value = "{type}/defaultPage", method = RequestMethod.POST)
    public RedirectView moveFile(Model model, @PathVariable String type, @RequestParam String page) {
        Site s = Site.fromSlug(type);

        AdminSites.canEdit(s);

        setInitialPage(page, s);

        return new RedirectView("/cms/pages/" + type, true);
    }

    @Atomic
    private void setInitialPage(String page, Site s) {
        s.setInitialPage(s.pageForSlug(page));
    }

}
