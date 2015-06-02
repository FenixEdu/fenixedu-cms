package org.fenixedu.cms.api.resource;

import java.util.stream.Stream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.api.json.CategoryAdapter;
import org.fenixedu.cms.api.json.MenuAdapter;
import org.fenixedu.cms.api.json.PageAdapter;
import org.fenixedu.cms.api.json.PostAdapter;
import org.fenixedu.cms.api.json.SiteAdapter;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("/cms/sites")
public class SiteResource extends BennuRestResource {

    //TODO check args in all methods
    //TODO: check permissions in all methods

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String listAllSites() {
        return view(getAdminSites(), SiteAdapter.class);
    }

    private Stream<Site> getAdminSites() {
        return Bennu.getInstance().getSitesSet().stream().filter(s -> s.getCanAdminGroup().isMember(Authenticate.getUser()));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String createSite(String json) {
        return view(createSiteFromJson(json));
    }

    private Site createSiteFromJson(String json) {
        return create(json, Site.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public String listSite(@PathParam("oid") Site site) {
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
    public String updateSite(@PathParam("oid") Site site, String json) {
        return updateSiteFromJson(site, json);
    }

    private String updateSiteFromJson(Site site, String json) {
        return view(update(json, site, SiteAdapter.class));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/posts")
    public String listSitePosts(@PathParam("oid") Site site) {
        return view(site.getPostSet(), PostAdapter.class);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/posts")
    public String createPost(@PathParam("oid") Site site, String json) {
        return view(createPostFromJson(site, json));
    }

    @Atomic(mode = TxMode.WRITE)
    private Post createPostFromJson(Site site, String json) {
        JsonObject jObj = new JsonParser().parse(json).getAsJsonObject();

        Post post = new Post(site);

        if (jObj.has("name") && !jObj.get("name").isJsonNull()) {
            post.setName(LocalizedString.fromJson(jObj.get("name")));
        }

        if (jObj.has("slug") && !jObj.get("slug").isJsonNull()) {
            post.setSlug(jObj.get("slug").getAsString());
        }

        if (jObj.has("body") && !jObj.get("body").isJsonNull()) {
            post.setBody(LocalizedString.fromJson(jObj.get("body")));
        }

        return post;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/pages")
    public String listSitePages(@PathParam("oid") Site site) {
        return view(site.getPagesSet(), PageAdapter.class);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/pages")
    public String createPage(@PathParam("oid") Site site, String json) {
        return view(createPageFromJson(site, json));
    }

    @Atomic(mode = TxMode.WRITE)
    private Page createPageFromJson(Site site, String json) {
        JsonObject jObj = new JsonParser().parse(json).getAsJsonObject();

        Page page = new Page(site);

        if (jObj.has("name") && !jObj.get("name").isJsonNull()) {
            page.setName(LocalizedString.fromJson(jObj.get("name")));
        }

        if (jObj.has("slug") && !jObj.get("slug").isJsonNull()) {
            page.setSlug(jObj.get("slug").getAsString());
        }

        if (jObj.has("published") && !jObj.get("published").isJsonNull()) {
            page.setPublished(jObj.get("published").getAsBoolean());
        }

        return page;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/categories")
    public String listSiteCategoriess(@PathParam("oid") Site site) {
        return view(site.getCategoriesSet(), CategoryAdapter.class);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/categories")
    public String createCategory(@PathParam("oid") Site site, String json) {
        return view(createCategoryFromJson(site, json));
    }

    @Atomic(mode = TxMode.WRITE)
    private Category createCategoryFromJson(Site site, String json) {
        JsonObject jObj = new JsonParser().parse(json).getAsJsonObject();

        Category category = new Category(site);

        if (jObj.has("name") && !jObj.get("name").isJsonNull()) {
            category.setName(LocalizedString.fromJson(jObj.get("name")));
        }

        if (jObj.has("slug") && !jObj.get("slug").isJsonNull()) {
            category.setSlug(jObj.get("slug").getAsString());
        }

        return category;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/menus")
    public String listSiteMenus(@PathParam("oid") Site site) {
        return view(site.getMenusSet(), MenuAdapter.class);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/menus")
    public String createMenu(@PathParam("oid") Site site, String json) {
        return view(createMenuFromJson(site, json));
    }

    @Atomic(mode = TxMode.WRITE)
    private Menu createMenuFromJson(Site site, String json) {
        JsonObject jObj = new JsonParser().parse(json).getAsJsonObject();

        Menu menu = new Menu(site);

        if (jObj.has("name") && !jObj.get("name").isJsonNull()) {
            menu.setName(LocalizedString.fromJson(jObj.get("name")));
        }

        if (jObj.has("slug") && !jObj.get("slug").isJsonNull()) {
            menu.setSlug(jObj.get("slug").getAsString());
        }

        if (jObj.has("topMenu") && !jObj.get("topMenu").isJsonNull()) {
            menu.setTopMenu(jObj.get("topMenu").getAsBoolean());
        }

        return menu;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/components")
    public String listSiteAvailableComponents(@PathParam("oid") Site site) {
        return new Gson().toJson(Component.availableComponents(site));
    }
}