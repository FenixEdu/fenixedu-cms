package org.fenixedu.cms.api.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.cms.api.json.MenuItemAdapter;
import org.fenixedu.cms.domain.MenuItem;

import com.google.gson.JsonElement;

@Path("/cms/menuItems")
public class MenuItemResource extends BennuRestResource {

    //TODO: check permissions in all methods

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public JsonElement getMenuItem(@PathParam("oid") MenuItem menuItem) {
        return view(menuItem, MenuItemAdapter.class);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public Response deleteMenuItem(@PathParam("oid") MenuItem menuItem) {
        menuItem.delete();
        return Response.ok().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public JsonElement updateMenuItem(@PathParam("oid") MenuItem menuItem, JsonElement json) {
        return updateMenuItemFromJson(menuItem, json);
    }

    private JsonElement updateMenuItemFromJson(MenuItem menuItem, JsonElement json) {
        return view(update(json, menuItem, MenuItemAdapter.class));
    }
}