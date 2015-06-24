package org.fenixedu.cms.api.resource;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.groups.LoggedGroup;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.cms.api.json.PostAdapter;
import org.fenixedu.cms.api.json.PostFileAdapter;
import org.fenixedu.cms.api.json.PostRevisionAdapter;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.PostFile;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.io.ByteStreams;

@Path("/cms/posts")
public class PostResource extends BennuRestResource {

    //TODO: check permissions in all methods

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public String listLatestVersion(@PathParam("oid") Post post) {
        return view(post, PostAdapter.class);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public Response deletePost(@PathParam("oid") Post post) {
        post.delete();
        return Response.ok().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public String updatePost(@PathParam("oid") Post post, String json) {
        return updatePostFromJson(post, json);
    }

    private String updatePostFromJson(Post post, String json) {
        return view(update(json, post, PostAdapter.class));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/versions")
    public String listPostVersions(@PathParam("oid") Post post) {
        return view(post.getRevisionsSet(), PostRevisionAdapter.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/files")
    public String listPostFiles(@PathParam("oid") Post post) {
        return view(post.getFilesSet(), PostFileAdapter.class);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/files")
    public String addPostFile(@PathParam("oid") Post post, @Context HttpServletRequest request) throws IOException,
            ServletException {
        Part part = request.getPart("file");

        createFileFromRequest(post, part);

        return view(post, PostAdapter.class);
    }

    @Atomic(mode = TxMode.WRITE)
    public void createFileFromRequest(Post post, Part part) throws IOException {
        GroupBasedFile groupBasedFile =
                new GroupBasedFile(part.getName(), part.getName(), ByteStreams.toByteArray(part.getInputStream()),
                        LoggedGroup.get());

        PostFile postFile = new PostFile(post, groupBasedFile, false, 0);
        post.addFiles(postFile);
    }
}