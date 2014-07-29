package org.fenixedu.bennu.cms.portal;

import org.fenixedu.bennu.cms.domain.Post;
import org.fenixedu.bennu.cms.domain.Site;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/posts")
public class AdminPosts {
    @RequestMapping(value = "{slug}", method = RequestMethod.GET)
    public String posts(Model model, @PathVariable(value = "slug") String slug) {
        Site site = Site.fromSlug(slug);
        model.addAttribute("site", site);
        model.addAttribute("posts", site.getPostSet());
        return "posts";
    }

    @RequestMapping(value = "{slug}/create", method = RequestMethod.GET)
    public String createPost(Model model, @PathVariable(value = "slug") String slug) {
        Site s = Site.fromSlug(slug);
        model.addAttribute("site", s);
        return "createPost";
    }

    @RequestMapping(value = "{slug}/create", method = RequestMethod.POST)
    public RedirectView createPost(Model model, @PathVariable(value = "slug") String slug, @RequestParam LocalizedString name,
            @RequestParam LocalizedString body, RedirectAttributes redirectAttributes) {
        if (name.isEmpty()) {
            redirectAttributes.addFlashAttribute("emptyName", true);
            return new RedirectView("/cms/posts/" + slug + "/create", true);
        } else {
            Site s = Site.fromSlug(slug);
            createPost(s, name, body);
            return new RedirectView("/cms/posts/" + s.getSlug() + "", true);
        }
    }

    @Atomic
    private void createPost(Site site, LocalizedString name, LocalizedString body) {
        Post p = new Post();

        p.setSite(site);
        p.setName(name);
        p.setBody(body);

    }

    @RequestMapping(value = "{slugSite}/{slugPost}/delete", method = RequestMethod.POST)
    public RedirectView delete(Model model, @PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "slugPost") String slugPost) {
        Site s = Site.fromSlug(slugSite);
        s.postForSlug(slugPost).delete();
        return new RedirectView("/cms/posts/" + s.getSlug() + "", true);
    }
}
