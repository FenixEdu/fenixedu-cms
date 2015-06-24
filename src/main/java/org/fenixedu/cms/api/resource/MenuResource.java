package org.fenixedu.cms.api.resource;

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

import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.cms.api.json.MenuAdapter;
import org.fenixedu.cms.api.json.MenuItemAdapter;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.MenuItem;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("/cms/menus")
public class MenuResource extends BennuRestResource {

    //TODO: check permissions in all methods

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public String getMenu(@PathParam("oid") Menu menu) {
        return view(menu, MenuAdapter.class);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public Response deleteMenu(@PathParam("oid") Menu menu) {
        menu.delete();
        return Response.ok().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public String updateMenu(@PathParam("oid") Menu menu, String json) {
        return updateMenuFromJson(menu, json);
    }

    private String updateMenuFromJson(Menu menu, String json) {
        return view(update(json, menu, MenuAdapter.class));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/menuItems")
    public String listMenuItems(@PathParam("oid") Menu menu) {
        return view(menu.getItemsSet(), MenuItemAdapter.class);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/menuItems")
    public String createMenuItem(@PathParam("oid") Menu menu, String json) {
        return view(createMenuItemFromJson(menu, json));
    }

    @Atomic(mode = TxMode.WRITE)
    private MenuItem createMenuItemFromJson(Menu menu, String json) {
        JsonObject jObj = new JsonParser().parse(json).getAsJsonObject();

        MenuItem menuItem = new MenuItem(menu);

        if (jObj.has("name") && !jObj.get("name").isJsonNull() && jObj.get("name").isJsonObject()) {
            menuItem.setName(LocalizedString.fromJson(jObj.get("name")));
        }

        if (jObj.has("position") && !jObj.get("position").isJsonNull()) {
            menuItem.setPosition(jObj.get("position").getAsInt());
        }

        if (jObj.has("folder") && !jObj.get("folder").isJsonNull()) {
            menuItem.setFolder(jObj.get("folder").getAsBoolean());
        }

        if (jObj.has("url") && !jObj.get("url").isJsonNull()) {
            menuItem.setUrl(jObj.get("url").getAsString());
        }

        return menuItem;
    }
}