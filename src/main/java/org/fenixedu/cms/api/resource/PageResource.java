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

import com.google.gson.JsonElement;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.cms.api.json.PageAdapter;
import org.fenixedu.cms.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.*;

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
        page.archive();
        return Response.ok().build();
    }
}