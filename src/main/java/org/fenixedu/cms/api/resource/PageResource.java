package org.fenixedu.cms.api.resource;

import com.google.gson.JsonElement;

import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.cms.api.json.PageAdapter;
import org.fenixedu.cms.domain.Page;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.DELETE_PAGE;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.EDIT_PAGE;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.SEE_PAGES;

@Path("/cms/pages")
public class PageResource extends BennuRestResource {

    //TODO: check permissions in all methods

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public JsonElement getPage(@PathParam("oid") Page page) {
        return view(page, PageAdapter.class);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public JsonElement updatePage(@PathParam("oid") Page page, JsonElement json) {
        return updatePageFromJson(page, json);
    }

    private JsonElement updatePageFromJson(Page page, JsonElement json) {
        return view(update(json, page, PageAdapter.class));
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public Response deletePage(@PathParam("oid") Page page) {
        ensureCanDoThis(page.getSite(), SEE_PAGES, EDIT_PAGE, DELETE_PAGE);
        page.delete();
        return Response.ok().build();
    }
}