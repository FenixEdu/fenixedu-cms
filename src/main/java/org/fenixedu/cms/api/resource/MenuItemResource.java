package org.fenixedu.cms.api.resource;

import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.DELETE_MENU_ITEM;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.EDIT_MENU;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.EDIT_MENU_ITEM;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.LIST_MENUS;

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

@Path("/cms/menuItems")
public class MenuItemResource extends BennuRestResource {

    //TODO: check permissions in all methods

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public String getMenuItem(@PathParam("oid") MenuItem menuItem) {
        return view(menuItem, MenuItemAdapter.class);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public Response deleteMenuItem(@PathParam("oid") MenuItem menuItem) {
        ensureCanDoThis(menuItem.getMenu().getSite(), LIST_MENUS, EDIT_MENU, EDIT_MENU_ITEM, DELETE_MENU_ITEM);
        menuItem.delete();
        return Response.ok().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public String updateMenuItem(@PathParam("oid") MenuItem menuItem, String json) {
        return updateMenuItemFromJson(menuItem, json);
    }

    private String updateMenuItemFromJson(MenuItem menuItem, String json) {
        return view(update(json, menuItem, MenuItemAdapter.class));
    }
}