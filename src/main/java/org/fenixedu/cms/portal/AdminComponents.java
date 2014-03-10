package org.fenixedu.cms.portal;



import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.ListCategoryPosts;
import org.fenixedu.cms.domain.ListOfCategories;
import org.fenixedu.cms.domain.ListPosts;
import org.fenixedu.cms.domain.MenuComponent;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.StaticPost;
import org.fenixedu.cms.domain.ViewPost;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@Controller
@RequestMapping("/cms/manage")
public class AdminComponents {

    @RequestMapping(value = "{slugSite}/pages/{slugPage}/createComponent", method = RequestMethod.POST)
    public RedirectView createComponent(Model model, @PathVariable(value = "slugSite") String slugSite, @PathVariable(
            value = "slugPage") String slugPage, @RequestParam String componentType,
            @RequestParam(required = false) String menuOid, @RequestParam(required = false) String catSlug, @RequestParam(required = false) String postSlug) {
        Site s = Site.fromSlug(slugSite);
        Page p = s.pageForSlug(slugPage);

        createComponent(componentType, menuOid, postSlug,catSlug, s, p);

        return new RedirectView("/cms/manage/" + s.getSlug() + "/pages/" + p.getSlug() + "/edit", true);
    }

    @Atomic(mode = TxMode.WRITE)
    private void createComponent(String componentType, String menuOid, String postSlug,String catSlug, Site s, Page p) {
        if (componentType.equals("viewPost")) {
            p.addComponents(new ViewPost());
        } else if (componentType.equals("listPost")) {
            p.addComponents(new ListPosts());
        } else if (componentType.equals("listCategories")) {
            p.addComponents(new ListOfCategories());
        } else if (componentType.equals("listCategoryPosts")) {
            Category cat = s.categoryForSlug(catSlug);
            ListCategoryPosts lcp = new ListCategoryPosts();
            lcp.setCategory(cat);
            p.addComponents(lcp);
        } else if (componentType.equals("menu")) {
            MenuComponent mc = new MenuComponent();
            mc.setMenu(s.menuForOid(menuOid));
            p.addComponents(mc);
        } else if (componentType.equals("staticPost")) {
            StaticPost mc = new StaticPost();
            mc.setPost(s.postForSlug(postSlug));
            p.addComponents(mc);
        }
    }

    @RequestMapping(value = "{slugSite}/pages/{slugPage}/deleteComponent/{oid}", method = RequestMethod.GET)
    public RedirectView deleteComponent(Model model, @PathVariable(value = "slugSite") String slugSite, @PathVariable(
            value = "slugPage") String slugPage, @PathVariable(
                    value = "oid") String oid) {
        
        Site s = Site.fromSlug(slugSite);
        Page p = s.pageForSlug(slugPage);
        p.componentForOid(oid).delete();

        return new RedirectView("/cms/manage/" + s.getSlug() + "/pages/" + p.getSlug() + "/edit", true);
    }
}
