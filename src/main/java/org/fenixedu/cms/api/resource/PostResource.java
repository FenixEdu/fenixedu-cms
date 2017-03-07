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

import com.google.common.io.ByteStreams;
import com.google.gson.JsonElement;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.cms.api.json.PostAdapter;
import org.fenixedu.cms.api.json.PostFileAdapter;
import org.fenixedu.cms.api.json.PostRevisionAdapter;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.PostFile;
import org.fenixedu.cms.ui.AdminPosts;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.*;

@Path("/cms/posts")
public class PostResource extends BennuRestResource {

    //TODO: check permissions in all methods

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public JsonElement listLatestVersion(@PathParam("oid") Post post) {
        return view(post, PostAdapter.class);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public Response deletePost(@PathParam("oid") Post post) {
        ensureCanDoThis(post.getSite(), EDIT_POSTS, DELETE_POSTS);
        if (post.isVisible()) {
            ensureCanDoThis(post.getSite(), EDIT_POSTS, DELETE_POSTS_PUBLISHED);
        }
        if (!Authenticate.getUser().equals(post.getCreatedBy())) {
            ensureCanDoThis(post.getSite(), EDIT_POSTS, DELETE_OTHERS_POSTS);
        }
        post.archive();
        return Response.ok().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public JsonElement updatePost(@PathParam("oid") Post post, JsonElement json) {
        return updatePostFromJson(post, json);
    }

    private JsonElement updatePostFromJson(Post post, JsonElement json) {
        return view(update(json, post, PostAdapter.class));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/versions")
    public JsonElement listPostVersions(@PathParam("oid") Post post) {
        return view(post.getRevisionsSet(), PostRevisionAdapter.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/files")
    public JsonElement listPostFiles(@PathParam("oid") Post post) {
        return view(post.getFilesSet(), PostFileAdapter.class);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/files")
    public JsonElement addPostFile(@PathParam("oid") Post post, @Context HttpServletRequest request)
            throws IOException, ServletException {
        createFileFromRequest(post, request.getPart("file"));
        return view(post, PostAdapter.class);
    }

    @Atomic(mode = TxMode.WRITE)
    public void createFileFromRequest(Post post, Part part) throws IOException {
        AdminPosts.ensureCanEditPost(post);
        GroupBasedFile groupBasedFile = new GroupBasedFile(part.getName(), part.getName(),
                part.getInputStream(), Group.logged());

        PostFile postFile = new PostFile(post, groupBasedFile, false, 0);
        post.addFiles(postFile);
    }
}