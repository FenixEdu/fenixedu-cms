package org.fenixedu.cms.api.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.cms.api.json.PostRevisionAdapter;
import org.fenixedu.cms.domain.PostContentRevision;

@Path("/cms/versions")
public class RevisionResource extends BennuRestResource {

    //TODO check args in all methods
    //TODO: check permissions in all methods

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public String listLatestVersion(@PathParam("oid") PostContentRevision revision) {
        return view(revision, PostRevisionAdapter.class);
    }

    //TODO revert?
}