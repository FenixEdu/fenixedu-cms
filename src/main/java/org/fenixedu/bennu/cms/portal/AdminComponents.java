package org.fenixedu.bennu.cms.portal;

import org.fenixedu.bennu.cms.domain.Category;
import org.fenixedu.bennu.cms.domain.ListCategoryPosts;
import org.fenixedu.bennu.cms.domain.ListOfCategories;
import org.fenixedu.bennu.cms.domain.ListPosts;
import org.fenixedu.bennu.cms.domain.MenuComponent;
import org.fenixedu.bennu.cms.domain.Page;
import org.fenixedu.bennu.cms.domain.Site;
import org.fenixedu.bennu.cms.domain.StaticPost;
import org.fenixedu.bennu.cms.domain.ViewPost;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/components")
public class AdminComponents {

    @RequestMapping(value = "{slugSite}/{slugPage}/createComponent", method = RequestMethod.POST)
    public RedirectView createComponent(Model model, @PathVariable(value = "slugSite") String slugSite, @PathVariable(
            value = "slugPage") String slugPage, @RequestParam String componentType,
            @RequestParam(required = false) String menuOid, @RequestParam(required = false) String catSlug, @RequestParam(
                    required = false) String postSlug) {
        Site s = Site.fromSlug(slugSite);
        AdminSites.canEdit(s);

        Page p = s.pageForSlug(slugPage);

        createComponent(componentType, menuOid, postSlug, catSlug, s, p);

        return new RedirectView("/cms/pages/" + s.getSlug() + "/" + p.getSlug() + "/edit", true);
    }

    @Atomic(mode = TxMode.WRITE)
    private void createComponent(String componentType, String menuOid, String postSlug, String catSlug, Site s, Page p) {
        if (componentType.equals("viewPost")) {
            p.addComponents(new ViewPost());
        } else if (componentType.equals("listPost")) {
            p.addComponents(new ListPosts());
        } else if (componentType.equals("listCategories")) {
            p.addComponents(new ListOfCategories());
        } else if (componentType.equals("listCategoryPosts")) {
            Category cat = s.categoryForSlug(catSlug);
            ListCategoryPosts lcp = new ListCategoryPosts(cat);
            p.addComponents(lcp);
        } else if (componentType.equals("menu")) {
            @SuppressWarnings("unused")
            MenuComponent mc = new MenuComponent(s.menuForOid(menuOid), p);
        } else if (componentType.equals("staticPost")) {
            StaticPost mc = new StaticPost();
            mc.setPost(s.postForSlug(postSlug));
            p.addComponents(mc);
        }
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/deleteComponent/{oid}", method = RequestMethod.POST)
    public RedirectView deleteComponent(Model model, @PathVariable(value = "slugSite") String slugSite, @PathVariable(
            value = "slugPage") String slugPage, @PathVariable(value = "oid") String oid) {

        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        Page p = s.pageForSlug(slugPage);
        p.componentForOid(oid).delete();

        return new RedirectView("/cms/pages/" + s.getSlug() + "/" + p.getSlug() + "/edit", true);
    }
}
