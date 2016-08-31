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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

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
    public JsonArray listAllTemplates() {

        JsonArray array = new JsonArray();

        Site.getTemplates().entrySet().forEach(entry -> {
            RegisterSiteTemplate registerSiteTemplate = entry.getValue();
            JsonObject json1 = new JsonObject();
            
            json1.addProperty("type", registerSiteTemplate.type());
            json1.addProperty("name", registerSiteTemplate.name());
            json1.addProperty("description", registerSiteTemplate.description());
            JsonObject json = json1;
            array.add(json);
        });

        return array;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{type}")
    public JsonObject listTemplate(@PathParam("type") String type) {
        RegisterSiteTemplate registerSiteTemplate = Site.getTemplates().get(type);

        if (registerSiteTemplate == null) {
            throw new WebApplicationException("Template not found for type: " + type, Status.NOT_FOUND);

        }
        JsonObject json = new JsonObject();
        
        json.addProperty("type", registerSiteTemplate.type());
        json.addProperty("name", registerSiteTemplate.name());
        json.addProperty("description", registerSiteTemplate.description());
        
        return json;
    }
}