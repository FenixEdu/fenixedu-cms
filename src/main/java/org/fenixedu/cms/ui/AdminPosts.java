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

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.*;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;
import pt.ist.fenixframework.FenixFramework;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.fenixedu.cms.ui.SearchUtils.searchPosts;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/posts")
public class AdminPosts {

    private static final int PER_PAGE = 10;
    private static final String JSON = "application/json;charset=utf-8";
    private static final JsonParser JSON_PARSER = new JsonParser();

    @Autowired
    AdminPostsService service;

    @RequestMapping(value = "{slug}", method = RequestMethod.GET)
    public String posts(Model model, @PathVariable String slug,
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

    @RequestMapping(value = "{slugSite}/{slugPost}/data", method = RequestMethod.GET, produces = JSON)
    public @ResponseBody String data(@PathVariable String slugSite, @PathVariable String slugPost) {
        Site s = Site.fromSlug(slugSite);
        AdminSites.canEdit(s);
        Post post = s.postForSlug(slugPost);

        JsonObject data = new JsonObject();
        data.add("post", service.serializePost(post));
        return data.toString();
    }

    @RequestMapping(value = "{slug}/create", method = RequestMethod.POST)
    public RedirectView createPost(@PathVariable(value = "slug") String slug, @RequestParam LocalizedString name) {
        Site s = Site.fromSlug(slug);
        AdminSites.canEdit(s);
        Post post = service.createPost(s, name);
        return new RedirectView("/cms/posts/" + s.getSlug() + "/" + post.getSlug() + "/edit", true);
    }

    @RequestMapping(value = "{siteSlug}/{postSlug}/edit", method = RequestMethod.GET)
    public String editPost(Model model, @PathVariable String siteSlug, @PathVariable String postSlug) {
        Site s = Site.fromSlug(siteSlug);
        AdminSites.canEdit(s);
        Post p = s.postForSlug(postSlug);
        model.addAttribute("site", s);
        model.addAttribute("post", p);
        return "fenixedu-cms/editPost";
    }


    @RequestMapping(value = "{slugSite}/{slugPost}/edit", method = RequestMethod.POST, consumes = JSON, produces = JSON)
    public @ResponseBody String edit(@PathVariable String slugSite, @PathVariable String slugPost, HttpEntity<String> httpEntity) {
        JsonObject postJson = JSON_PARSER.parse(httpEntity.getBody()).getAsJsonObject();
        Site site = Site.fromSlug(slugSite);
        Post post = site.postForSlug(slugPost);
        service.processPostChanges(site, post, postJson);
        return data(site.getSlug(), post.getSlug());
    }

    @RequestMapping(value = "{slugSite}/{slugPost}/delete", method = RequestMethod.POST)
    public RedirectView delete(@PathVariable String slugSite, @PathVariable String slugPost) {
        Site s = Site.fromSlug(slugSite);
        FenixFramework.atomic(() -> {
            AdminSites.canEdit(s);
            s.postForSlug(slugPost).delete();
        });
        return new RedirectView("/cms/posts/" + s.getSlug() + "", true);
    }


    @RequestMapping(value = "{slugSite}/{slugPost}/files", method = RequestMethod.POST, produces = JSON)
    public @ResponseBody String addFile(@PathVariable String slugSite, @PathVariable String slugPost,
                    @RequestParam String name, @RequestParam boolean embedded, @RequestParam MultipartFile file) {
        Site s = Site.fromSlug(slugSite);
        AdminSites.canEdit(s);
        Post p = s.postForSlug(slugPost);
        PostFile postFile = service.createFile(p, name, embedded, p.getCanViewGroup(), file);
        return service.serializePostFile(postFile).toString();
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
        json.addProperty("next", ofNullable(revision.getNext()).map(x -> x.getExternalId()).orElse(null));
        json.addProperty("previous", ofNullable(revision.getPrevious()).map(x -> x.getExternalId()).orElse(null));

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

        FenixFramework.atomic(()-> {
            p.setBody(revision.getBody());
        });

        // SiteActivity.revertedToRevision

        return new RedirectView("/cms/posts/" + s.getSlug() + "/" + p.getSlug() + "/edit", true);
    }



}
