/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of FenixEdu CMS.
 *
 * FenixEdu CMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu CMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu CMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.cms.ui;

import static org.fenixedu.cms.ui.SearchUtils.searchPosts;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.io.servlet.FileDownloadServlet;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.PostContentRevision;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/posts")
public class AdminPosts {

    private static final int PER_PAGE = 10;

    @RequestMapping(value = "{slug}", method = RequestMethod.GET)
    public String posts(Model model, @PathVariable(value = "slug") String slug,
                    @RequestParam(required = false, defaultValue = "1") int page,
                    @RequestParam(required = false) String query, @RequestParam(required = false) String category,
                    @RequestParam(required = false, defaultValue = "false") boolean showAll) {

        Site site = Site.fromSlug(slug);
        AdminSites.canEdit(site);

        Collection<Post> posts = site.getPostSet();
        if (!Strings.isNullOrEmpty(category)) {
            Category cat = site.categoryForSlug(category);
            posts = cat.getPostsSet();
            model.addAttribute("category", cat);
        }

        if(!showAll) {
            posts = posts.stream().filter(post -> !post.isStaticPost()).collect(Collectors.toList());
        }

        if (!Strings.isNullOrEmpty(query)) {
            posts = searchPosts(posts, query);
        }

        SearchUtils.Partition<Post> partition = new SearchUtils.Partition<>(posts, Post.CREATION_DATE_COMPARATOR, PER_PAGE, page);

        model.addAttribute("site", site);
        model.addAttribute("query", query);

        model.addAttribute("partition", partition);
        model.addAttribute("posts", partition.getItems());

        return "fenixedu-cms/posts";
    }

    @RequestMapping(value = "{slug}/create", method = RequestMethod.POST)
    public RedirectView createPost(@PathVariable(value = "slug") String slug, @RequestParam LocalizedString name) {
        Site s = Site.fromSlug(slug);
        AdminSites.canEdit(s);
        Post post = createPost(s, name);
        return new RedirectView("/cms/posts/" + s.getSlug() + "/" + post.getSlug() + "/edit", true);
    }

    @Atomic
    private Post createPost(Site site, LocalizedString name) {
        Post p = new Post(site);
        p.setName(Post.sanitize(name));
        p.setBody(new LocalizedString());
        p.setCanViewGroup(site.getCanViewGroup());
        p.setActive(false);
        return p;
    }

    @RequestMapping(value = "{slug}/{postSlug}/edit", method = RequestMethod.GET)
    public String editPost(Model model, @PathVariable(value = "slug") String slug,
            @PathVariable(value = "postSlug") String postSlug) {
        Site s = Site.fromSlug(slug);
        AdminSites.canEdit(s);
        Post p = s.postForSlug(postSlug);
        model.addAttribute("site", s);
        model.addAttribute("post", p);
        return "fenixedu-cms/editPost";
    }

    @RequestMapping(value = "{slug}/{postSlug}/createCategory", method = RequestMethod.POST)
    public RedirectView createCategory(@PathVariable(value = "slug") String slug,
            @PathVariable(value = "postSlug") String postSlug, @RequestParam LocalizedString name) {
        Site site = Site.fromSlug(slug);
        AdminSites.canEdit(site);
        Post post = site.postForSlug(postSlug);

        FenixFramework.atomic(() -> {
            Category p = new Category(site, name);
        });

        return new RedirectView("/cms/posts/" + site.getSlug() + "/" + post.getSlug() + "/edit", true);
    }

    @RequestMapping(value = "{slug}/{postSlug}/edit", method = RequestMethod.POST)
    public RedirectView editPost(@PathVariable(value = "slug") String slug, @PathVariable(value = "postSlug") String postSlug,
            @RequestParam String newSlug, @RequestParam LocalizedString name, @RequestParam LocalizedString body, @RequestParam(
                    required = false) String[] categories,
                    @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) DateTime publicationStarts, @RequestParam(
                            required = false) @DateTimeFormat(iso = ISO.DATE_TIME) DateTime publicationEnds, @RequestParam(
                                    required = false, defaultValue = "false") boolean active, @RequestParam String viewGroup,
                                    RedirectAttributes redirectAttributes) {


        if (name.isEmpty()) {
            redirectAttributes.addFlashAttribute("emptyName", true);
            return new RedirectView("/cms/posts/" + slug + "/create", true);
        } else {
            Site s = Site.fromSlug(slug);
            Post p = s.postForSlug(postSlug);
            editPost(p, name, body, newSlug, categories, publicationStarts, publicationEnds, active, Group.parse(viewGroup));
            return new RedirectView("/cms/posts/" + s.getSlug() + "/" + p.getSlug() + "/edit", true);
        }
    }

    @Atomic
    private void editPost(Post post, LocalizedString name, LocalizedString body, String newSlug, String[] categories,
            DateTime publicationStarts, DateTime publicationEnds, boolean active, Group viewGroup) {
        post.setName(Post.sanitize(name));
        post.setBody(Post.sanitize(body));
        post.setSlug(newSlug);
        post.getCategoriesSet().clear();
        HashSet<String> h = new HashSet<>();
        if (categories == null) {
            categories = new String[0];
        }
        h.addAll(Arrays.asList(categories));
        post.getCategoriesSet().addAll(
                post.getSite().getCategoriesSet().stream().filter(x -> h.contains(x.getSlug())).collect(Collectors.toList()));

        post.setPublicationBegin(publicationStarts);
        post.setPublicationEnd(publicationEnds);
        post.setActive(active);
        post.setCanViewGroup(viewGroup);
    }

    @RequestMapping(value = "{slugSite}/{slugPost}/delete", method = RequestMethod.POST)
    public RedirectView delete(@PathVariable String slugSite, @PathVariable String slugPost) {

        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        s.postForSlug(slugPost).delete();
        return new RedirectView("/cms/posts/" + s.getSlug() + "", true);
    }

    @RequestMapping(value = "{slugSite}/{slugPost}/addAttachment", method = RequestMethod.POST)
    public RedirectView addAttachment(@PathVariable String slugSite, @PathVariable String slugPost,
            @RequestParam(required = true) String name, @RequestParam MultipartFile attachment) throws IOException {

        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        Post p = s.postForSlug(slugPost);

        addAttachment(name, attachment, p);

        return new RedirectView("/cms/posts/" + s.getSlug() + "/" + p.getSlug() + "/edit#attachments", true);
    }

    @RequestMapping(value = "{slugSite}/{slugPost}/addAttachment.json", method = RequestMethod.POST,
            produces = "application/json")
    public @ResponseBody String addAttachmentJson(@PathVariable String slugSite,
                                                @PathVariable String slugPost,
                                                @RequestParam(required = true) String name,
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
        GroupBasedFile f = new GroupBasedFile(name, attachment.getOriginalFilename(), attachment.getBytes(), Group.anyone());
        p.addAttachment(f, 0);
        return f;
    }

    @RequestMapping(value = "{slugSite}/{slugPost}/deleteAttachment", method = RequestMethod.POST)
    public RedirectView deleteAttachment(@PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "slugPost") String slugPost, @RequestParam Integer file) throws IOException {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        Post p = s.postForSlug(slugPost);

        FenixFramework.atomic(() -> p.getFilesSorted().get(file).delete());

        return new RedirectView("/cms/posts/" + s.getSlug() + "/" + p.getSlug() + "/edit#attachments", true);
    }

    @RequestMapping(value = "{slugSite}/{slugPost}/moveAttachment", method = RequestMethod.POST)
    public RedirectView moveAttachment(@PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "slugPost") String slugPost, @RequestParam Integer origin, @RequestParam Integer destiny)
            throws IOException {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        Post p = s.postForSlug(slugPost);

        FenixFramework.atomic(() -> p.moveFile(origin, destiny));

        return new RedirectView("/cms/posts/" + s.getSlug() + "/" + p.getSlug() + "/edit#attachments", true);
    }

    @RequestMapping(value = "{slugSite}/{slugPost}/addFile.json", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String addFileJson(@PathVariable String slugSite, @PathVariable String slugPost,
            @RequestParam MultipartFile attachment) throws IOException {
        Site s = Site.fromSlug(slugSite);
        AdminSites.canEdit(s);
        Post p = s.postForSlug(slugPost);
        GroupBasedFile f = addFile(attachment, p);

        JsonObject json = new JsonObject();
        json.addProperty("displayname", f.getDisplayName());
        json.addProperty("filename", f.getFilename());
        json.addProperty("url", FileDownloadServlet.getDownloadUrl(f));

        return json.toString();
    }

    @Atomic
    private GroupBasedFile addFile(MultipartFile attachment, Post p) throws IOException {
        String filename = attachment.getOriginalFilename();
        GroupBasedFile f = new GroupBasedFile(filename, filename, attachment.getBytes(), Group.anyone());
        p.addEmbeddedFile(f, 0);
        return f;
    }

    @RequestMapping(value = "{slugSite}/{slugPost}/versions", method = RequestMethod.GET)
    public String versions(Model model, @PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "slugPost") String slugPost) {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        Post p = s.postForSlug(slugPost);
        model.addAttribute("post", p);
        model.addAttribute("site", s);
        return "fenixedu-cms/versions";
    }

    @RequestMapping(value = "{slugSite}/{slugPost}/versionData", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String versionData(@PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "slugPost") String slugPost, @RequestParam(required = false) PostContentRevision revision) {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        Post p = s.postForSlug(slugPost);

        if (revision == null) {
            revision = p.getLatestRevision();
        }

        if (revision.getPost() != p) {
            throw new RuntimeException("Invalid Revision");
        }

        JsonObject json = new JsonObject();

        json.add("content", revision.getBody().json());
        json.addProperty("modifiedAt", revision.getRevisionDate().toString());
        json.addProperty("user", revision.getCreatedBy().getUsername());
        json.addProperty("userName", revision.getCreatedBy().getProfile().getDisplayName());
        json.addProperty("id", revision.getExternalId());
        json.addProperty("next", Optional.ofNullable(revision.getNext()).map(x -> x.getExternalId()).orElse(null));
        json.addProperty("previous", Optional.ofNullable(revision.getPrevious()).map(x -> x.getExternalId()).orElse(null));

        if (revision.getPrevious() != null) {
            json.add("previousContent", revision.getPrevious().getBody().json());
        }

        return json.toString();
    }

    @RequestMapping(value = "{slugSite}/{slugPost}/revertTo", method = RequestMethod.POST)
    public RedirectView revertTo(Model model, @PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "slugPost") String slugPost, @RequestParam PostContentRevision revision) {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        Post p = s.postForSlug(slugPost);

        if (revision.getPost() != p) {
            throw new RuntimeException("Invalid Revision");
        }

        FenixFramework.atomic(() -> {
            p.setBody(revision.getBody());
        });

        // SiteActivity.revertedToRevision

        return new RedirectView("/cms/posts/" + s.getSlug() + "/" + p.getSlug() + "/edit", true);
    }
}
