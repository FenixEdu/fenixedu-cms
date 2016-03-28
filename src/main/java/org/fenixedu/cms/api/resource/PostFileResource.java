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
import org.fenixedu.cms.api.json.PostFileAdapter;
import org.fenixedu.cms.domain.PostFile;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.EDIT_PAGE;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.EDIT_POSTS;

@Path("/cms/postFiles")
public class PostFileResource extends BennuRestResource {

    //TODO: check permissions in all methods

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public JsonElement getPostFile(@PathParam("oid") PostFile postFile) {
        return view(postFile, PostFileAdapter.class);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public JsonElement updatePostFile(@PathParam("oid") PostFile postFile, JsonElement json) {
        return updatePostFileFromJson(postFile, json);
    }

    private JsonElement updatePostFileFromJson(PostFile postFile, JsonElement json) {
        return view(update(json, postFile, PostFileAdapter.class));
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public Response deletePostFile(@PathParam("oid") PostFile postFile) {
        if (postFile.getPost() != null && postFile.getPost().isStaticPost()) {
            ensureCanDoThis(postFile.getSite(), EDIT_PAGE);
        } else {
            ensureCanDoThis(postFile.getSite(), EDIT_POSTS);
        }
        postFile.delete();
        return Response.ok().build();
    }
}