package org.fenixedu.cms.api.resource;

import com.google.gson.JsonElement;

import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.cms.api.json.ComponentAdapter;
import org.fenixedu.cms.domain.component.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/cms/components")
public class ComponentResource extends BennuRestResource {

    //TODO: check permissions in all methods

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public JsonElement getComponent(@PathParam("oid") Component component) {
        return view(component, ComponentAdapter.class);
    }
}