package org.fenixedu.bennu.cms.portal;

import org.fenixedu.bennu.cms.domain.*;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
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

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/pages")
public class AdminPages {

    @RequestMapping(value = "{slug}", method = RequestMethod.GET)
    public String pages(Model model, @PathVariable(value = "slug") String slug) {
        Site site = Site.fromSlug(slug);

        AdminSites.canEdit(site);

        model.addAttribute("site", site);
        model.addAttribute("pages", site.getPagesSet());
        return "pages";
    }

    @RequestMapping(value = "{slug}/create", method = RequestMethod.GET)
    public String createPage(Model model, @PathVariable(value = "slug") String slug) {
        Site s = Site.fromSlug(slug);

        AdminSites.canEdit(s);

        model.addAttribute("site", s);
        return "createPage";
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

            createPage(name, s);
            return new RedirectView("/cms/pages/" + s.getSlug(), true);
        }
    }

    @Atomic
    private void createPage(String name, Site s) {
        Page p = new Page();
        p.setSite(s);
        p.setName(new LocalizedString(I18N.getLocale(), name));
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/edit", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "slugPage") String slugPage) {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        if (slugPage.equals("--**--")){
            slugPage = "";
        }

        Page p = s.pageForSlug(slugPage);
        model.addAttribute("site", s);
        model.addAttribute("page", p);

        return "editPage";
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/edit", method = RequestMethod.POST)
    public RedirectView edit(Model model, @PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "slugPage") String slugPage, @RequestParam LocalizedString name, @RequestParam String slug,
            @RequestParam String template, RedirectAttributes redirectAttributes) {
        if (name != null && name.isEmpty()) {
            redirectAttributes.addFlashAttribute("emptyName", true);
            return new RedirectView("/cms/pages/" + slugSite + "/" + slugPage + "/edit", true);
        }
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        Page p = s.pageForSlug(slugPage);
        editPage(name, slug, template, s, p);
        return new RedirectView("/cms/pages/" + s.getSlug() + "", true);
    }

    @Atomic(mode = TxMode.WRITE)
    private void editPage(LocalizedString name, String slug, String template, Site s, Page p) {
        p.setName(name);
        p.setSlug(slug);
        if (s != null && s.getTheme() != null) {
            CMSTemplate t = s.getTheme().templateForType(template);
            p.setTemplate(t);
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

        return new RedirectView("/cms/pages/" + type , true);
    }

    @Atomic
    private void setInitialPage(String page, Site s) {
        s.setInitialPage(s.pageForSlug(page));
    }

}
