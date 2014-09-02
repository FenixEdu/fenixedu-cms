package org.fenixedu.bennu.cms.portal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.cms.domain.Site;
import org.fenixedu.bennu.cms.routing.CMSURLHandler;
import org.fenixedu.bennu.core.domain.Bennu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by nurv on 26/08/14.
 */
@Controller
public class DefaultSiteController {

    private final CMSURLHandler handler;

    @Autowired
    public DefaultSiteController(CMSURLHandler handler) {
        this.handler = handler;
    }

    @RequestMapping("/")
    public void handleDefaultSite(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Site s = Bennu.getInstance().getDefaultSite();

        if (s != null) {
            handler.handleRequest(s, req, resp, "/");
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
