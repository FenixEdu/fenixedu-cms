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

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.groups.AnyoneGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.io.servlets.FileDownloadServlet;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.MenuItem;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.component.StaticPost;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import static org.fenixedu.cms.domain.MenuItem.CREATION_DATE_COMPARATOR;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/pages")
public class AdminPages {

    @RequestMapping(value = "{slug}", method = RequestMethod.GET)
    public String pages(Model model, @PathVariable(value = "slug") String slug, @RequestParam(required = false) String query) {
        Site site = Site.fromSlug(slug);

        AdminSites.canEdit(site);
        model.addAttribute("query", query);
        Collection<Page> pages = getStaticPages(site);
        if (!Strings.isNullOrEmpty(query)) {
            pages = SearchUtils.searchPages(pages, query);
        }
        model.addAttribute("site", site);
        model.addAttribute("pages", pages);
        return "fenixedu-cms/pages";
    }

    @RequestMapping(value = "{slug}/create", method = RequestMethod.GET)
    public String createPage(Model model, @PathVariable(value = "slug") String slug) {
        Site site = Site.fromSlug(slug);
        AdminSites.canEdit(site);
        model.addAttribute("site", site);
        return "fenixedu-cms/createPage";
    }

    @RequestMapping(value = "{slug}/create", method = RequestMethod.POST)
    public RedirectView createPage(@PathVariable(value = "slug") String slug, @RequestParam LocalizedString name) {
        Site site = Site.fromSlug(slug);
        AdminSites.canEdit(site);
        Page page = createPageAndPost(name, site);
        return pageRedirect(page);
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/edit", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "slugPage") String slugPage) {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        if (slugPage.equals("--**--")) {
            slugPage = "";
        }

        Page p = s.pageForSlug(slugPage);
        model.addAttribute("site", s);
        model.addAttribute("page", p);
        model.addAttribute("post", p.getStaticPost().get());
        model.addAttribute("availableComponents", Component.availableComponents(s));
        Optional<MenuItem> menuItemOptional = p.getMenuItemsSet().stream().sorted(CREATION_DATE_COMPARATOR).findFirst();
        model.addAttribute("menuItem", menuItemOptional.orElse(null));
        model.addAttribute("menu", menuItemOptional.map(MenuItem::getMenu).orElse(null));

        return "fenixedu-cms/editPage";
    }

    @RequestMapping(value = "{slug}/{pageSlug}/createCategory", method = RequestMethod.POST)
    public RedirectView createCategory(@PathVariable String slug, @PathVariable String pageSlug,
            @RequestParam LocalizedString name) {
        Site site = Site.fromSlug(slug);
        AdminSites.canEdit(site);
        Page page = site.pageForSlug(pageSlug);

        FenixFramework.atomic(() -> {
            Category p = new Category(site, name);
        });

        return pageRedirect(page);
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/edit", method = RequestMethod.POST)
    public RedirectView edit(@PathVariable String slugSite, @PathVariable String slugPage,
                             @RequestParam String newSlug, @RequestParam LocalizedString name,
                             @RequestParam LocalizedString body, @RequestParam(required = false) String[] categories,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime publicationStarts,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime publicationEnds,
                             @RequestParam(required = false, defaultValue = "false") boolean active, @RequestParam String viewGroup,
                             @RequestParam(required = false) String menu, @RequestParam(required = false) String menuItem,
                             @RequestParam(required = false) String menuItemParent, @RequestParam(required = false) Integer menuItemPosition) {
        Site site = Site.fromSlug(slugSite);
        Page page = site.pageForSlug(slugPage);
        Menu menuObj = getDomainObjectIfPresent(menu);
        MenuItem menuItemObj = getDomainObjectIfPresent(menuItem);
        MenuItem menuItemParentObj = getDomainObjectIfPresent(menuItemParent);
        page.getStaticPost().ifPresent(post ->
                editPageAndPost(page, post, name, body, newSlug, categories, publicationStarts, publicationEnds, active,
                        Group.parse(viewGroup), menuObj, menuItemObj, menuItemParentObj, menuItemPosition));
        return pageRedirect(page);
    }

    private <T extends DomainObject> T getDomainObjectIfPresent(String externalId) {
        return Optional.ofNullable(externalId).map(oid -> (T) FenixFramework.getDomainObject(oid))
                .filter(FenixFramework::isDomainObjectValid).orElse(null);
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/delete", method = RequestMethod.POST)
    public RedirectView delete(@PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "slugPage") String slugPage) {
        Site s = Site.fromSlug(slugSite);
        AdminSites.canEdit(s);
        Page page = s.pageForSlug(slugPage);
        FenixFramework.atomic(() -> {
            page.getStaticPost().ifPresent(Post::delete);
            page.delete();
        });
        return allPagesRedirect(s);
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/addAttachment", method = RequestMethod.POST)
    public RedirectView addAttachment(@PathVariable String slugSite, @PathVariable String slugPage,
            @RequestParam(required = true) String name, @RequestParam MultipartFile attachment) {

        Site site = Site.fromSlug(slugSite);

        AdminSites.canEdit(site);
        Page page = site.pageForSlug(slugPage);
        page.getStaticPost().ifPresent(post -> {
            try {
                addAttachment(name, attachment, post);
            } catch (IOException e) {
                //TODO: add error message
            }
        });

        return pageRedirect(page);
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/addAttachment.json", method = RequestMethod.POST,
            produces = "application/json")
    public @ResponseBody String addAttachmentJson(@PathVariable String slugSite, @PathVariable String slugPage, @RequestParam(
            required = true) String name, @RequestParam("attachment") MultipartFile attachment) throws IOException {
        Site site = Site.fromSlug(slugSite);
        AdminSites.canEdit(site);
        Page page = site.pageForSlug(slugPage);
        JsonObject obj = new JsonObject();
        page.getStaticPost().ifPresent(post -> FenixFramework.atomic(() -> {
            try {
                GroupBasedFile f = new GroupBasedFile(name, name, attachment.getBytes(), AnyoneGroup.get());
                post.addAttachment(f, 0);
                obj.addProperty("displayname", f.getDisplayName());
                obj.addProperty("filename", f.getFilename());
                obj.addProperty("url", FileDownloadServlet.getDownloadUrl(f));
            } catch (IOException e) {
                //TODO: add error message
            }
        }));
        return obj.toString();
    }

    @Atomic
    private GroupBasedFile addAttachment(String name, MultipartFile attachment, Post p) throws IOException {
        GroupBasedFile f = new GroupBasedFile(name, attachment.getOriginalFilename(), attachment.getBytes(), AnyoneGroup.get());
        p.addAttachment(f, 0);
        return f;
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/deleteAttachment", method = RequestMethod.POST)
    public RedirectView deleteAttachment(@PathVariable String slugSite, @PathVariable String slugPage, @RequestParam Integer file) {
        Site site = Site.fromSlug(slugSite);
        AdminSites.canEdit(site);
        Page page = site.pageForSlug(slugPage);
        page.getStaticPost().ifPresent(post -> FenixFramework.atomic(() -> post.getFilesSorted().get(file).delete()));
        return pageRedirect(page);
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/moveAttachment", method = RequestMethod.POST)
    public RedirectView moveAttachment(@PathVariable String slugSite, @PathVariable String slugPage,
            @RequestParam Integer origin, @RequestParam Integer destiny) throws IOException {
        Site site = Site.fromSlug(slugSite);
        AdminSites.canEdit(site);
        Page page = site.pageForSlug(slugPage);
        page.getStaticPost().ifPresent(post -> FenixFramework.atomic(() -> post.moveFile(origin, destiny)));
        return pageRedirect(page);
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/addFile.json", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String addFileJson(@PathVariable String slugSite, @PathVariable String slugPage,
            @RequestParam MultipartFile[] attachment) {
        Site site = Site.fromSlug(slugSite);
        AdminSites.canEdit(site);
        Page page = site.pageForSlug(slugPage);
        JsonArray array = new JsonArray();

        page.getStaticPost().ifPresent(post -> Stream.of(attachment).map(multipartFile -> {
            JsonObject obj = new JsonObject();
            FenixFramework.atomic(() -> {
                String filename = multipartFile.getOriginalFilename();
                try {
                    GroupBasedFile f = new GroupBasedFile(filename, filename, multipartFile.getBytes(), AnyoneGroup.get());
                    post.addEmbeddedFile(f, 0);
                    obj.addProperty("displayname", f.getDisplayName());
                    obj.addProperty("filename", f.getFilename());
                    obj.addProperty("url", FileDownloadServlet.getDownloadUrl(f));
                } catch (IOException e) {
                    //TODO - add error message
                }
            });
            return obj;
        }).filter(obj -> !obj.isJsonNull()).forEach(x -> array.add(x)));

        return array.toString();
    }

    private Collection<Page> getStaticPages(Site site) {
        return site.getPagesSet().stream().filter(Page::isStaticPage).sorted(Page.PAGE_NAME_COMPARATOR)
                .collect(Collectors.toList());
    }

    public RedirectView allPagesRedirect(Site site) {
        return new RedirectView("/cms/pages/" + site.getSlug() + "", true);
    }

    public RedirectView pageRedirect(Page page) {
        return new RedirectView("/cms/pages/" + page.getSite().getSlug() + "/" + page.getSlug() + "/edit", true);
    }

    @Atomic
    private void editPageAndPost(Page page, Post post, LocalizedString name, LocalizedString body, String newSlug, String[] categories,
                                 DateTime publicationStarts, DateTime publicationEnds, boolean active, Group viewGroup, Menu menu, MenuItem menuItem, MenuItem menuItemParent, Integer menuItemPosition) {
        Site site = page.getSite();
        LocalizedString nameSanitized = Post.sanitize(name);
        LocalizedString bodySanitized = Post.sanitize(body);

        Optional.ofNullable(categories).ifPresent(
                categoriesSlugs -> Stream.of(categoriesSlugs).map(site::categoryForSlug).filter(category -> category != null)
                .forEach(post::addCategories));

        if (!post.getName().equals(nameSanitized)) {
            post.setName(nameSanitized);
        }
        if (!post.getBody().equals(bodySanitized)) {
            post.setBody(bodySanitized);
        }
        if (!post.getSlug().equals(newSlug)) {
            post.setSlug(newSlug);
        }

        if (post.getPublicationBegin() != publicationStarts) {
            post.setPublicationBegin(publicationStarts);
        }

        if (post.getPublicationEnd() != publicationEnds) {
            post.setPublicationEnd(publicationEnds);
        }

        if (post.getActive() != active) {
            post.setActive(active);
        }

        if (!post.getCanViewGroup().equals(viewGroup)) {
            post.setCanViewGroup(viewGroup);
        }

        if (!page.getName().equals(nameSanitized)) {
            page.setName(nameSanitized);
        }

        if (!page.getSlug().equals(newSlug)) {
            page.setSlug(newSlug);
        }

        if (!page.getPublished() != active) {
            page.setPublished(active);
        }

        if (!page.getCanViewGroup().equals(viewGroup)) {
            page.setCanViewGroup(viewGroup);
        }

        if (menu == null) {
            page.getMenuItemsSet().stream().forEach(MenuItem::delete);
        } else if (menu != null && menuItem == null) {
            menuItem = new MenuItem(menu);
        }

        if (menu != null && menuItem != null) {
            menuItem.setName(name);
            menuItem.setPage(page);
            menuItem.setFolder(page == null);
            if (menuItemParent != null) {
                menuItemParent.putAt(menuItem, menuItemPosition);
            } else {
                menu.putAt(menuItem, menuItemPosition);
            }
        }

    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    private Page createPageAndPost(LocalizedString name, Site site) {
        Page page = new Page(site, name);
        Post post = new Post(site);
        post.setName(name);
        post.setBody(new LocalizedString());
        page.addComponents(new StaticPost(post));
        page.setTemplateType("view");
        return page;
    }
}
