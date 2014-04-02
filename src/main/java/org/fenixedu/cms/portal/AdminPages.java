package org.fenixedu.cms.portal;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.CMSTemplate;
import org.fenixedu.cms.domain.Page;
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

@BennuSpringController(AdminPortal.class)
@RequestMapping("/cms/manage")
public class AdminPages {

    @RequestMapping(value = "{slug}/pages", method = RequestMethod.GET)
    public String pages(Model model, @PathVariable(value = "slug") String slug) {
        Site site = Site.fromSlug(slug);
        model.addAttribute("site", site);
        model.addAttribute("pages", site.getPagesSet());
        return "pages";
    }

    @RequestMapping(value = "{slug}/pages/create", method = RequestMethod.GET)
    public String createPage(Model model, @PathVariable(value = "slug") String slug) {
        Site s = Site.fromSlug(slug);
        model.addAttribute("site", s);
        return "createPage";
    }

    @RequestMapping(value = "{slug}/pages/create", method = RequestMethod.POST)
    public RedirectView createPage(Model model, @PathVariable(value = "slug") String slug, @RequestParam String name,
            RedirectAttributes redirectAttributes) {
        if (Strings.isNullOrEmpty(name)) {
            redirectAttributes.addFlashAttribute("emptyName", true);
            return new RedirectView("/cms/manage/" + slug + "/pages/create", true);
        } else {
            Site s = Site.fromSlug(slug);
            createPage(name, s);
            return new RedirectView("/cms/manage/" + s.getSlug() + "/pages", true);
        }
    }

    @Atomic
    private void createPage(String name, Site s) {
        Page p = new Page();
        p.setSite(s);
        p.setName(new LocalizedString(I18N.getLocale(), name));
    }

    @RequestMapping(value = "{slugSite}/pages/{slugPage}/edit", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "slugPage") String slugPage) {
        Site s = Site.fromSlug(slugSite);
        Page p = s.pageForSlug(slugPage);
        model.addAttribute("site", s);
        model.addAttribute("page", p);

        return "editPage";
    }

    @RequestMapping(value = "{slugSite}/pages/{slugPage}/edit", method = RequestMethod.POST)
    public RedirectView edit(Model model, @PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "slugPage") String slugPage, @RequestParam String name, @RequestParam String slug,
            @RequestParam String template, RedirectAttributes redirectAttributes) {
        if (Strings.isNullOrEmpty(name)) {
            redirectAttributes.addFlashAttribute("emptyName", true);
            return new RedirectView("/cms/manage/" + slugSite + "/pages/" + slugPage + "/edit", true);
        }
        Site s = Site.fromSlug(slugSite);
        Page p = s.pageForSlug(slugPage);
        editPage(name, slug, template, s, p);
        return new RedirectView("/cms/manage/" + s.getSlug() + "/pages", true);
    }

    @Atomic(mode = TxMode.WRITE)
    private void editPage(String name, String slug, String template, Site s, Page p) {
        p.setName(new LocalizedString(I18N.getLocale(), name));
        p.setSlug(slug);
        if (s != null && s.getTheme() != null) {
            CMSTemplate t = s.getTheme().templateForType(template);
            p.setTemplate(t);
        }
    }

    @RequestMapping(value = "{slugSite}/pages/{slugPage}/delete", method = RequestMethod.POST)
    public RedirectView delete(Model model, @PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "slugPage") String slugPage) {
        Site s = Site.fromSlug(slugSite);
        s.pageForSlug(slugPage).delete();
        return new RedirectView("/cms/manage/" + s.getSlug() + "/pages", true);
    }
}
