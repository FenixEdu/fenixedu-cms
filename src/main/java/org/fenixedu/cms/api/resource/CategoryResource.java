package org.fenixedu.cms.api.resource;

import com.google.gson.JsonElement;

import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.cms.api.json.CategoryAdapter;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.PermissionEvaluation;
import org.fenixedu.cms.domain.PermissionsArray.Permission;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/cms/categories")
public class CategoryResource extends BennuRestResource {

    //TODO: check permissions in all methods

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public JsonElement getCategory(@PathParam("oid") Category category) {
        return view(category, CategoryAdapter.class);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public JsonElement updateCategory(@PathParam("oid") Category category, JsonElement json) {
        return updateCategoryFromJson(category, json);
    }

    private JsonElement updateCategoryFromJson(Category category, JsonElement json) {
        return view(update(json, category, CategoryAdapter.class));
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public Response deleteCategory(@PathParam("oid") Category category) {
        PermissionEvaluation.ensureCanDoThis(category.getSite(), Permission.DELETE_CATEGORY);
        category.delete();
        return Response.ok().build();
    }
}