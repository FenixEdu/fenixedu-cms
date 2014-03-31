package org.fenixedu.cms.portal;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(AdminPortal.class)
@RequestMapping("/cms/manage")
public class AdminCategory {
    @RequestMapping(value="{slug}/categories", method = RequestMethod.GET)
    public String categories(Model model, @PathVariable(value="slug") String slug){
        Site site = Site.fromSlug(slug);
        model.addAttribute("site", site);
        model.addAttribute("categories", site.getCategoriesSet());
        return "categories";
    }
    
    @RequestMapping(value="{slug}/categories/create", method = RequestMethod.GET)
    public String createCategory(Model model, @PathVariable(value="slug") String slug){
        Site s = Site.fromSlug(slug);
        model.addAttribute("site", s);
        return "createCategory";
    }
    
    @RequestMapping(value="{slug}/categories/create", method = RequestMethod.POST)
    public RedirectView createCategory(Model model, @PathVariable(value="slug") String slug, @RequestParam String name){
        Site s = Site.fromSlug(slug);
        createCategory(s, name);
        return new RedirectView("/cms/manage/" + s.getSlug() + "/categories",true);
    }
    
    @Atomic
    private void createCategory(Site site, String name) {
        Category p = new Category();
        p.setSite(site);
        p.setName(new LocalizedString(I18N.getLocale(),name));
    }
    
    @RequestMapping(value="{slugSite}/categories/{slugCategories}/delete", method = RequestMethod.GET)
    public RedirectView delete(Model model, @PathVariable(value="slugSite") String slugSite, @PathVariable(value="slugCategories") String slugCategories){
        Site s = Site.fromSlug(slugSite);
        s.categoryForSlug(slugCategories).delete();
        return new RedirectView("/cms/manage/" + s.getSlug() + "/categories",true);
    }
}
