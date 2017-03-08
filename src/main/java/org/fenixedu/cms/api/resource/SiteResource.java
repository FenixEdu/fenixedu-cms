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
package org.fenixedu.cms.api.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.domain.Bennu;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.api.json.*;
import org.fenixedu.cms.domain.*;
import org.fenixedu.cms.domain.PermissionsArray.Permission;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.fenixedu.commons.i18n.LocalizedString;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/cms/sites")
public class SiteResource extends BennuRestResource {

    //TODO: check permissions in all methods

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement listAllSites() {
        return view(getAdminSites(), SiteAdapter.class);
    }

    private Stream<Site> getAdminSites() {
        return Bennu.getInstance().getSitesSet().stream().filter(s -> PermissionEvaluation.canAccess(Authenticate.getUser(), s));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement createSite(JsonElement json) {
        CmsSettings.getInstance().ensureCanManageSettings();
        return view(createSiteFromJson(json));
    }

    private Site createSiteFromJson(JsonElement json) {
        return create(json, Site.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public JsonElement listSite(@PathParam("oid") Site site) {
        return view(site, SiteAdapter.class);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public Response deleteSite(@PathParam("oid") Site site) {
        site.delete();
        return Response.ok().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public JsonElement updateSite(@PathParam("oid") Site site, JsonElement json) {
        return updateSiteFromJson(site, json);
    }

    private JsonElement updateSiteFromJson(Site site, JsonElement json) {
        return view(update(json, site, SiteAdapter.class));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/posts")
    public JsonElement listSitePosts(@PathParam("oid") Site site, @QueryParam("category") final Set<Category> categories) {
        Set<Post> posts = site.getPostSet();

        if (categories != null && !categories.isEmpty()) {
            posts = posts.stream().filter(p -> p.getCategoriesSet().stream().anyMatch(c -> categories.contains(c)))
                    .collect(Collectors.toSet());
        }

        return view(posts, PostAdapter.class);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/posts")
    public JsonElement createPost(@PathParam("oid") Site site, JsonObject json) {
        return view(createPostFromJson(site, json));
    }

    @Atomic(mode = TxMode.WRITE)
    private Post createPostFromJson(Site site, JsonObject jObj) {
        PermissionEvaluation.ensureCanDoThis(site, Permission.CREATE_POST);
        Post post = new Post(site);

        if (jObj.has("name") && !jObj.get("name").isJsonNull() && jObj.get("name").isJsonObject()) {
            post.setName(LocalizedString.fromJson(jObj.get("name")));
        }

        if (jObj.has("slug") && !jObj.get("slug").isJsonNull()) {
            post.setSlug(jObj.get("slug").getAsString());
        }

        if (jObj.has("body") && !jObj.get("body").isJsonNull() && jObj.get("body").isJsonObject()) {

            LocalizedString excerpt = null;
            if(jObj.has("excerpt") && !jObj.get("excerpt").isJsonNull() && jObj.get("excerpt").isJsonObject()) {
                excerpt = LocalizedString.fromJson(jObj.get("excerpt"));
            }

            post.setBodyAndExcerpt(LocalizedString.fromJson(jObj.get("body")), excerpt);
        }

        return post;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/pages")
    public JsonElement listSitePages(@PathParam("oid") Site site) {
        return view(site.getPagesSet(), PageAdapter.class);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/pages")
    public JsonElement createPage(@PathParam("oid") Site site, JsonObject json) {
        return view(createPageFromJson(site, json));
    }

    @Atomic(mode = TxMode.WRITE)
    private Page createPageFromJson(Site site, JsonObject jObj) {
        PermissionEvaluation.ensureCanDoThis(site, Permission.CREATE_PAGE);
        if (jObj.has("name") && !jObj.get("name").isJsonNull() && jObj.get("name").isJsonObject()) {
            Page page = new Page(site, LocalizedString.fromJson(jObj.get("name")));

            if (jObj.has("slug") && !jObj.get("slug").isJsonNull()) {
                page.setSlug(jObj.get("slug").getAsString());
            }

            if (jObj.has("published") && !jObj.get("published").isJsonNull()) {
                page.setPublished(jObj.get("published").getAsBoolean());
            }
            return page;
        }
        throw CmsDomainException.badRequest("page.missing.name");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/categories")
    public JsonElement listSiteCategories(@PathParam("oid") Site site) {
        return view(site.getCategoriesSet(), CategoryAdapter.class);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/categories")
    public JsonElement createCategory(@PathParam("oid") Site site, JsonObject json) {
        return view(createCategoryFromJson(site, json));
    }

    @Atomic(mode = TxMode.WRITE)
    private Category createCategoryFromJson(Site site, JsonObject jObj) {

        if (jObj.has("name") && !jObj.get("name").isJsonNull() && jObj.get("name").isJsonObject()) {
            PermissionEvaluation.ensureCanDoThis(site, Permission.CREATE_CATEGORY);
            Category category = new Category(site, LocalizedString.fromJson(jObj.get("name")));
            if (jObj.has("slug") && !jObj.get("slug").isJsonNull()) {
                category.setSlug(jObj.get("slug").getAsString());
            }
            return category;
        }

        throw CmsDomainException.badRequest("category.missing.name");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/menus")
    public JsonElement listSiteMenus(@PathParam("oid") Site site) {
        return view(site.getOrderedMenusSet(), MenuAdapter.class);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/menus")
    public JsonElement createMenu(@PathParam("oid") Site site, JsonObject json) {
        return view(createMenuFromJson(site, json));
    }

    @Atomic(mode = TxMode.WRITE)
    private Menu createMenuFromJson(Site site, JsonObject jObj) {
        if (jObj.has("name") && !jObj.get("name").isJsonNull() && jObj.get("name").isJsonObject()) {
            PermissionEvaluation.ensureCanDoThis(site, Permission.CREATE_MENU);
            Menu menu = new Menu(site, LocalizedString.fromJson(jObj.get("name")));

            if (jObj.has("slug") && !jObj.get("slug").isJsonNull()) {
                menu.setSlug(jObj.get("slug").getAsString());
            }

            if (jObj.has("topMenu") && !jObj.get("topMenu").isJsonNull()) {
                menu.setTopMenu(jObj.get("topMenu").getAsBoolean());
            }
            return menu;

        }

        throw CmsDomainException.badRequest("menu.missing.name");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/components")
    public JsonArray listSiteAvailableComponents(@PathParam("oid") Site site) {
        return Component.availableComponents(site);
    }
}
