package org.fenixedu.cms.portal;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
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
public class AdminPosts {
    @RequestMapping(value="{slug}/posts", method = RequestMethod.GET)
    public String posts(Model model, @PathVariable(value="slug") String slug){
        Site site = Site.fromSlug(slug);
        model.addAttribute("site", site);
        model.addAttribute("posts", site.getPostSet());
        return "posts";
    }
    
    @RequestMapping(value="{slug}/posts/create", method = RequestMethod.GET)
    public String createPost(Model model, @PathVariable(value="slug") String slug){
        Site s = Site.fromSlug(slug);
        model.addAttribute("site", s);
        return "createPost";
    }
    
    @RequestMapping(value="{slug}/posts/create", method = RequestMethod.POST)
    public RedirectView createPost(Model model, @PathVariable(value="slug") String slug, @RequestParam String name, @RequestParam String body){
        Site s = Site.fromSlug(slug);
        createPost(s, name, body);
        return new RedirectView("/cms/manage/" + s.getSlug() + "/posts",true);
    }
    
    @Atomic
    private void createPost(Site site, String name, String body) {
        Post p = new Post();
        
        p.setSite(site);
        p.setName(new LocalizedString(I18N.getLocale(), name));
        p.setBody(new LocalizedString(I18N.getLocale(), body));
        
    }
    
    @RequestMapping(value="{slugSite}/posts/{slugPost}/delete", method = RequestMethod.GET)
    public RedirectView delete(Model model, @PathVariable(value="slugSite") String slugSite, @PathVariable(value="slugPost") String slugPost){
        Site s = Site.fromSlug(slugSite);
        s.postForSlug(slugPost).delete();
        return new RedirectView("/cms/manage/" + s.getSlug() + "/posts",true);
    }
}
