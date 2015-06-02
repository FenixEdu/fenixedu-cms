package org.fenixedu.cms.api.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.cms.domain.RegisterSiteTemplate;
import org.fenixedu.cms.domain.Site;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Path("/cms/templates")
public class TemplateResource extends BennuRestResource {

    //TODO check args in all methods
    //TODO: check permissions in all methods

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String listAllTemplates() {

        JsonArray array = new JsonArray();

        Site.getTemplates().entrySet().forEach(entry -> {
            JsonObject json = jsonFromTemplate(entry.getValue());
            array.add(json);
        });

        return view(array);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{type}")
    public String listTemplate(@PathParam("type") String type) {
        RegisterSiteTemplate registerSiteTemplate = Site.getTemplates().get(type);
        JsonObject json = jsonFromTemplate(registerSiteTemplate);

        return json.toString();
    }

    private JsonObject jsonFromTemplate(RegisterSiteTemplate registerSiteTemplate) {
        JsonObject json = new JsonObject();

        json.addProperty("type", registerSiteTemplate.type());
        json.addProperty("name", registerSiteTemplate.name());
        json.addProperty("description", registerSiteTemplate.description());

        return json;
    }
}