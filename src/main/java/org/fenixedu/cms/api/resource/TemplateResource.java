package org.fenixedu.cms.api.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.cms.domain.RegisterSiteTemplate;
import org.fenixedu.cms.domain.Site;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Path("/cms/templates")
public class TemplateResource extends BennuRestResource {

    //TODO: check permissions in all methods

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String listAllTemplates() {

        JsonArray array = new JsonArray();

        Site.getTemplates().entrySet().forEach(entry -> {
            JsonObject json = jsonFromTemplate(entry.getValue());
            array.add(json);
        });

        return array.toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{type}")
    public Response listTemplate(@PathParam("type") String type) {
        RegisterSiteTemplate registerSiteTemplate = Site.getTemplates().get(type);

        if (registerSiteTemplate == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Template not found for type: " + type).build();
        }
        JsonObject json = jsonFromTemplate(registerSiteTemplate);

        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    private JsonObject jsonFromTemplate(RegisterSiteTemplate registerSiteTemplate) {
        JsonObject json = new JsonObject();

        json.addProperty("type", registerSiteTemplate.type());
        json.addProperty("name", registerSiteTemplate.name());
        json.addProperty("description", registerSiteTemplate.description());

        return json;
    }
}