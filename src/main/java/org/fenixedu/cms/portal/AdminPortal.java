package org.fenixedu.cms.portal;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.cms.domain.CMSTheme;
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
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.base.Strings;

@SpringApplication(group = "anyone", path = "cms", title = "application.title")
@SpringFunctionality(app = AdminPortal.class, title = "application.admin-portal.title")
@RequestMapping("/cms/manage")
public class AdminPortal {

    @RequestMapping
    public String list(Model model) {
        model.addAttribute("sites", Bennu.getInstance().getSitesSet());
        return "manage";
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String create(Model model) {
        model.addAttribute("templates", Site.getTemplates());
        return "create";
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public RedirectView create(Model model, @RequestParam String name, @RequestParam String description,
            @RequestParam String template, RedirectAttributes redirectAttributes) {
        if (Strings.isNullOrEmpty(name)) {
            redirectAttributes.addFlashAttribute("emptyName", true);
            return new RedirectView("/cms/manage/create", true);
        } else {
            createSite(name, description, template);
            return new RedirectView("/cms/manage", true);
        }
    }

    @Atomic
    private void createSite(String name, String description, String template) {
        Site site = new Site();
        site.setBennu(Bennu.getInstance());
        site.setDescription(new LocalizedString(I18N.getLocale(), description));
        site.setName(new LocalizedString(I18N.getLocale(), name));

        if (!template.equals("null")) {
            Site.templateFor(template).makeIt(site);
        }
    }

    @RequestMapping(value = "{slug}/edit", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable(value = "slug") String slug) {
        model.addAttribute("site", Site.fromSlug(slug));
        model.addAttribute("themes", Bennu.getInstance().getCMSThemesSet());
        return "editSite";

    }

    @RequestMapping(value = "{slug}/edit", method = RequestMethod.POST)
    public RedirectView edit(Model model, @PathVariable(value = "slug") String slug, @RequestParam String name,
            @RequestParam String description, @RequestParam String theme, RedirectAttributes redirectAttributes) {
        if (Strings.isNullOrEmpty(name)) {
            redirectAttributes.addFlashAttribute("emptyName", true);
            return new RedirectView("/cms/manage/" + slug + "/edit", true);
        } else {
            editSite(name, description, theme, Site.fromSlug(slug));
            return new RedirectView("/cms/manage", true);
        }
    }

    @Atomic(mode = TxMode.WRITE)
    private void editSite(String name, String description, String theme, Site s) {
        s.setName(new LocalizedString(I18N.getLocale(), name));
        s.setDescription(new LocalizedString(I18N.getLocale(), description));
        s.setTheme(CMSTheme.forType(theme));
    }

    //THIS NEEDS REVEIEW
    @RequestMapping(value = "{slug}/delete", method = RequestMethod.POST)
    public RedirectView delete(@PathVariable(value = "slug") String slug) {
        Site s = Site.fromSlug(slug);
        s.delete();
        return new RedirectView("/cms/manage", true);
    }

}