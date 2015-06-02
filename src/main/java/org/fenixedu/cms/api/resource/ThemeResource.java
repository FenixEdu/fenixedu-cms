package org.fenixedu.cms.api.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.cms.api.json.ThemeAdapter;
import org.fenixedu.cms.domain.CMSTheme;

@Path("/cms/themes")
public class ThemeResource extends BennuRestResource {

    //TODO check args in all methods
    //TODO: check permissions in all methods

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String listAllThemes() {
        return view(Bennu.getInstance().getCMSThemesSet(), ThemeAdapter.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public String listTheme(@PathParam("oid") CMSTheme theme) {
        return view(theme, ThemeAdapter.class);
    }
}