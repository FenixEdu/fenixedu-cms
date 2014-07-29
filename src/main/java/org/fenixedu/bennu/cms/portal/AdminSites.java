package org.fenixedu.bennu.cms.portal;

import org.fenixedu.bennu.cms.domain.CMSTheme;
import org.fenixedu.bennu.cms.domain.Site;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
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

@SpringApplication(group = "anyone", path = "cms", title = "application.title")
@SpringFunctionality(app = AdminSites.class, title = "application.admin-portal.title")
@RequestMapping("/cms/sites")
public class AdminSites {

    @RequestMapping
    public String list(Model model) {
        model.addAttribute("sites", Bennu.getInstance().getSitesSet());
        return "manage";
    }

    @RequestMapping(value = "{slug}/edit", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable(value = "slug") String slug) {
        model.addAttribute("site", Site.fromSlug(slug));
        model.addAttribute("themes", Bennu.getInstance().getCMSThemesSet());
        return "editSite";

    }

    @RequestMapping(value = "{slug}/edit", method = RequestMethod.POST)
    public RedirectView edit(Model model, @PathVariable(value = "slug") String slug, @RequestParam LocalizedString name,
            @RequestParam LocalizedString description, @RequestParam String theme, @RequestParam String newSlug, @RequestParam(
                    required = false) Boolean published, RedirectAttributes redirectAttributes) {
        if (name.isEmpty()) {
            redirectAttributes.addFlashAttribute("emptyName", true);
            return new RedirectView("/sites/" + slug + "/edit", true);
        } else {
            if (published == null) {
                published = false;
            }
            editSite(name, description, theme, newSlug, published, Site.fromSlug(slug));
            return new RedirectView("/cms/sites", true);
        }
    }

    @Atomic(mode = TxMode.WRITE)
    private void editSite(LocalizedString name, LocalizedString description, String theme, String slug, Boolean published, Site s) {
        s.setName(name);
        s.setDescription(description);
        s.setTheme(CMSTheme.forType(theme));
        if (!s.getSlug().equals(slug)) {
            s.setSlug(slug);
        }
        s.setPublished(published);
    }

    //THIS NEEDS REVEIEW
    @RequestMapping(value = "{slug}/delete", method = RequestMethod.POST)
    public RedirectView delete(@PathVariable(value = "slug") String slug) {
        Site s = Site.fromSlug(slug);
        s.delete();
        return new RedirectView("/cms/sites", true);
    }

}