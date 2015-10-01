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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.PermissionsArray.Permission;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.PostMetadata;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import pt.ist.fenixframework.FenixFramework;

import static java.util.stream.Collectors.toList;
import static org.fenixedu.cms.domain.PermissionEvaluation.canDoThis;
import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;
import static org.fenixedu.cms.ui.SearchUtils.searchPages;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/pages")
public class AdminPages {

    private static final int PER_PAGE = 10;
    private static final String JSON = "application/json;charset=utf-8";
    private static final JsonParser JSON_PARSER = new JsonParser();

    @Autowired
    AdminPagesService service;

    @RequestMapping(value = "{slug}", method = RequestMethod.GET)
    public String pages(Model model, @PathVariable String slug, @RequestParam(required = false) String query,
                    @RequestParam(required = false, defaultValue = "1") int currentPage) {
        Site site = Site.fromSlug(slug);
        AdminSites.canEdit(site);
        ensureCanDoThis(site, Permission.SEE_PAGES);
        Collection<Page> allPages = Strings.isNullOrEmpty(query) ? getStaticPages(site) : searchPages(getStaticPages(site), query);
        SearchUtils.Partition<Page> partition =
                        new SearchUtils.Partition<>(allPages, Page.CREATION_DATE_COMPARATOR, PER_PAGE, currentPage);

        model.addAttribute("site", site);
        model.addAttribute("query", query);
        model.addAttribute("partition", partition);
        model.addAttribute("pages", partition.getItems());
        return "fenixedu-cms/pages";
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/edit", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable String slugSite, @PathVariable String slugPage) {
        Site site = Site.fromSlug(slugSite);
        AdminSites.canEdit(site);
        ensureCanDoThis(site, Permission.SEE_PAGES, Permission.EDIT_PAGE);
        Page page = site.pageForSlug(slugPage);
        model.addAttribute("site", site);
        model.addAttribute("page", page);
        model.addAttribute("post", page.getStaticPost().get());
        return "fenixedu-cms/editPage";
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/data", method = RequestMethod.GET, produces = JSON)
    public @ResponseBody String data(@PathVariable String slugSite, @PathVariable String slugPage) {
        Site site = Site.fromSlug(slugSite);
        AdminSites.canEdit(site);
        ensureCanDoThis(site, Permission.SEE_PAGES, Permission.EDIT_PAGE);
        Page page = site.pageForSlug(slugPage);
        JsonObject data = new JsonObject();
        JsonArray menus = new JsonArray();
        if(canDoThis(site, Permission.LIST_MENUS, Permission.EDIT_MENU)) {
            site.getMenusSet().stream().sorted(Comparator.comparing(Menu::getName))
                .map(service::serializeMenu).forEach(menus::add);
        }
        data.add("post", service.serializePage(page));
        data.add("menus", menus);
        return data.toString();
    }

    @RequestMapping(value = "{slug}/create", method = RequestMethod.POST)
    public RedirectView createPage(@PathVariable String slug, @RequestParam LocalizedString name) {
        Site site = Site.fromSlug(slug);
        AdminSites.canEdit(site);
        ensureCanDoThis(site, Permission.SEE_PAGES, Permission.EDIT_PAGE, Permission.CREATE_PAGE);
        Page page = service.createPageAndPost(name, site);
        return pageRedirect(page);
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/edit", method = RequestMethod.POST, consumes = JSON, produces = JSON)
    public @ResponseBody String edit(@PathVariable String slugSite, @PathVariable String slugPage, HttpEntity<String> httpEntity) {
        Site site = Site.fromSlug(slugSite);
        ensureCanDoThis(site, Permission.SEE_PAGES, Permission.EDIT_PAGE);
        JsonObject editData = JSON_PARSER.parse(httpEntity.getBody()).getAsJsonObject();
        Page page = site.pageForSlug(slugPage);
        service.processChanges(site, page, editData);
        return data(site.getSlug(), page.getSlug());
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/delete", method = RequestMethod.POST)
    public RedirectView delete(@PathVariable String slugSite, @PathVariable String slugPage) {
        Site s = Site.fromSlug(slugSite);
        AdminSites.canEdit(s);
        Page page = s.pageForSlug(slugPage);
        FenixFramework.atomic(() -> {
            ensureCanDoThis(page.getSite(), Permission.EDIT_PAGE, Permission.DELETE_PAGE);
            page.getStaticPost().ifPresent(Post::delete);
            page.delete();
        });
        return allPagesRedirect(s);
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/metadata", method = RequestMethod.GET)
    public String viewEditMetadata(Model model, @PathVariable String slugSite, @PathVariable String slugPage) {
        Site s = Site.fromSlug(slugSite);
        AdminSites.canEdit(s);
        ensureCanDoThis(s, Permission.EDIT_PAGE, Permission.SEE_METADATA, Permission.EDIT_METADATA);
        Page page  = s.pageForSlug(slugPage);
        Post post = page.getStaticPost().get();
        model.addAttribute("site", s);
        model.addAttribute("page", page);
        model.addAttribute("post", post);
        model.addAttribute("metadata", Optional.ofNullable(post.getMetadata()).map(PostMetadata::json).map(
            JsonElement::toString).orElseGet(()->new JsonObject().toString()));
        return "fenixedu-cms/editMetadata";
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/metadata", method = RequestMethod.POST)
    public RedirectView editMetadata(@PathVariable String slugSite,
                                         @PathVariable String slugPage,
                                         @RequestParam String metadata) {
        Site s = Site.fromSlug(slugSite);
        Page page = s.pageForSlug(slugPage);
        FenixFramework.atomic(()-> {
            AdminSites.canEdit(s);
            ensureCanDoThis(s, Permission.EDIT_PAGE, Permission.SEE_METADATA, Permission.EDIT_METADATA);
            page.getStaticPost().ifPresent(
                post -> post.setMetadata(PostMetadata.internalize(metadata)));
        });
        return new RedirectView("/cms/pages/" + s.getSlug() + "/" + page.getSlug() + "/metadata", true);
    }

    private Collection<Page> getStaticPages(Site site) {
        return site.getPagesSet().stream().filter(Page::isStaticPage).sorted(Page.PAGE_NAME_COMPARATOR)
                .collect(toList());
    }

    public RedirectView allPagesRedirect(Site site) {
        return new RedirectView("/cms/pages/" + site.getSlug() + "", true);
    }

    public RedirectView pageRedirect(Page page) {
        return new RedirectView("/cms/pages/" + page.getSite().getSlug() + "/" + page.getSlug() + "/edit", true);
    }

}
