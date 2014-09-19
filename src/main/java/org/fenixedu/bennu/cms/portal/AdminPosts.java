package org.fenixedu.bennu.cms.portal;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.fenixedu.bennu.cms.domain.Post;
import org.fenixedu.bennu.cms.domain.Site;
import org.fenixedu.bennu.core.groups.AnyoneGroup;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.io.servlets.FileDownloadServlet;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.Atomic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/posts")
public class AdminPosts {
    @RequestMapping(value = "{slug}", method = RequestMethod.GET)
    public String posts(Model model, @PathVariable(value = "slug") String slug) {
        Site site = Site.fromSlug(slug);

        AdminSites.canEdit(site);

        model.addAttribute("site", site);
        model.addAttribute("posts", site.getPostSet());
        return "posts";
    }

    @RequestMapping(value = "{slug}/create", method = RequestMethod.GET)
    public String createPost(Model model, @PathVariable(value = "slug") String slug) {
        Site s = Site.fromSlug(slug);

        AdminSites.canEdit(s);

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

            AdminSites.canEdit(s);

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

    @RequestMapping(value = "{slug}/{postSlug}/edit", method = RequestMethod.GET)
    public String editPost(Model model, @PathVariable(value = "slug") String slug,
            @PathVariable(value = "postSlug") String postSlug) {
        Site s = Site.fromSlug(slug);

        AdminSites.canEdit(s);

        Post p = s.postForSlug(postSlug);
        model.addAttribute("site", s);
        model.addAttribute("post", p);
        return "editPost";
    }

    @RequestMapping(value = "{slug}/{postSlug}/edit", method = RequestMethod.POST)
    public RedirectView editPost(Model model, @PathVariable(value = "slug") String slug,
            @PathVariable(value = "postSlug") String postSlug, @RequestParam String newSlug, @RequestParam LocalizedString name,
            @RequestParam LocalizedString body, @RequestParam String[] categories, RedirectAttributes redirectAttributes) {

        if (name.isEmpty()) {
            redirectAttributes.addFlashAttribute("emptyName", true);
            return new RedirectView("/cms/posts/" + slug + "/create", true);
        } else {
            Site s = Site.fromSlug(slug);
            Post p = s.postForSlug(postSlug);
            editPost(p, name, body, newSlug, categories);
            return new RedirectView("/cms/posts/" + s.getSlug() + "", true);
        }
    }

    @Atomic
    private void editPost(Post post, LocalizedString name, LocalizedString body, String newSlug, String[] categories) {
        post.setName(name);
        post.setBody(body);
        post.setSlug(newSlug);
        post.getCategoriesSet().clear();
        HashSet<String> h = new HashSet<>();
        h.addAll(Arrays.asList(categories));
        post.getCategoriesSet().addAll(
                post.getSite().getCategoriesSet().stream().filter(x -> h.contains(x.getSlug())).collect(Collectors.toList()));
    }

    @RequestMapping(value = "{slugSite}/{slugPost}/delete", method = RequestMethod.POST)
    public RedirectView delete(Model model, @PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "slugPost") String slugPost) {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        s.postForSlug(slugPost).delete();
        return new RedirectView("/cms/posts/" + s.getSlug() + "", true);
    }

    @RequestMapping(value = "{slugSite}/{slugPost}/addAttachment", method = RequestMethod.POST)
    public RedirectView addAttachment(Model model, @PathVariable(value = "slugSite") String slugSite, @PathVariable(
            value = "slugPost") String slugPost, @RequestParam(required = true) String name,
            @RequestParam("attachment") MultipartFile attachment) throws IOException {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        Post p = s.postForSlug(slugPost);

        addAttachment(name, attachment, p);

        return new RedirectView("/cms/posts/" + s.getSlug() + "/" + p.getSlug() + "/edit#attachments", true);
    }

    @RequestMapping(value = "{slugSite}/{slugPost}/addAttachment.json", method = RequestMethod.POST,
            produces = "application/json")
    public @ResponseBody String addAttachmentJson(Model model, @PathVariable(value = "slugSite") String slugSite, @PathVariable(
            value = "slugPost") String slugPost, @RequestParam(required = true) String name,
            @RequestParam("attachment") MultipartFile attachment) throws IOException {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        Post p = s.postForSlug(slugPost);

        GroupBasedFile f = addAttachment(name, attachment, p);
        JsonObject obj = new JsonObject();
        obj.addProperty("displayname", f.getDisplayName());
        obj.addProperty("filename", f.getFilename());
        obj.addProperty("url", FileDownloadServlet.getDownloadUrl(f));

        return obj.toString();
    }

    @Atomic
    private GroupBasedFile addAttachment(String name, MultipartFile attachment, Post p) throws IOException {
        GroupBasedFile f = new GroupBasedFile(name, attachment.getOriginalFilename(), attachment.getBytes(), AnyoneGroup.get());

        p.getAttachments().putFile(f, 0);
        return f;
    }

    @RequestMapping(value = "{slugSite}/{slugPost}/deleteAttachment", method = RequestMethod.POST)
    public RedirectView deleteAttachment(Model model, @PathVariable(value = "slugSite") String slugSite, @PathVariable(
            value = "slugPost") String slugPost, @RequestParam Integer file) throws IOException {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        Post p = s.postForSlug(slugPost);

        deleteAttachment(file, p);

        return new RedirectView("/cms/posts/" + s.getSlug() + "/" + p.getSlug() + "/edit#attachments", true);
    }

    @Atomic
    private void deleteAttachment(Integer file, Post p) {
        p.getAttachments().removeFile(file).delete();
    }

    @RequestMapping(value = "{slugSite}/{slugPost}/moveAttachment", method = RequestMethod.POST)
    public RedirectView moveAttachment(Model model, @PathVariable(value = "slugSite") String slugSite, @PathVariable(
            value = "slugPost") String slugPost, @RequestParam Integer origin, @RequestParam Integer destiny) throws IOException {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        Post p = s.postForSlug(slugPost);

        moveAttachment(origin, destiny, p);

        return new RedirectView("/cms/posts/" + s.getSlug() + "/" + p.getSlug() + "/edit#attachments", true);
    }

    @Atomic
    private void moveAttachment(Integer origin, Integer destiny, Post p) {
        p.getAttachments().move(origin, destiny);
    }

    @RequestMapping(value = "{slugSite}/{slugPost}/addFile.json", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String addFileJson(Model model, @PathVariable(value = "slugSite") String slugSite, @PathVariable(
            value = "slugPost") String slugPost, @RequestParam("attachment") MultipartFile[] attachments) throws IOException {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        Post p = s.postForSlug(slugPost);
        JsonArray array = new JsonArray();

        Arrays.asList(attachments).stream().map((attachment) -> {
            GroupBasedFile f = null;
            try {
                f = addFile(attachment, p);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JsonObject obj = new JsonObject();
            obj.addProperty("displayname", f.getDisplayName());
            obj.addProperty("filename", f.getFilename());
            obj.addProperty("url", FileDownloadServlet.getDownloadUrl(f));
            return obj;
        }).forEach(x -> array.add(x));

        return array.toString();
    }

    @Atomic
    private GroupBasedFile addFile(MultipartFile attachment, Post p) throws IOException {
        GroupBasedFile f =
                new GroupBasedFile(attachment.getOriginalFilename(), attachment.getOriginalFilename(), attachment.getBytes(),
                        AnyoneGroup.get());
        p.getPostFiles().putFile(f);
        return f;
    }

    @RequestMapping(value = "{slugSite}/{slugPost}/deleteFile", method = RequestMethod.POST)
    public RedirectView deleteFile(Model model, @PathVariable(value = "slugSite") String slugSite, @PathVariable(
            value = "slugPost") String slugPost, @RequestParam GroupBasedFile file) throws IOException {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        Post p = s.postForSlug(slugPost);

        deleteFile(file, p);

        return new RedirectView("/cms/posts/" + s.getSlug() + "/" + p.getSlug() + "/edit#files", true);
    }

    @Atomic
    private void deleteFile(GroupBasedFile file, Post p) {
        if (p.getPostFiles().contains(file)) {
            p.getPostFiles().removeFile(file);
            file.delete();
        }
    }
}
