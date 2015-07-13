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
import org.fenixedu.cms.api.json.PostFileAdapter;
import org.fenixedu.cms.domain.PostFile;

import com.google.gson.JsonElement;

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
        postFile.delete();
        return Response.ok().build();
    }
}