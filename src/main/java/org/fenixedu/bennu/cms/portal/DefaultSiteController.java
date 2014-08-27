package org.fenixedu.bennu.cms.portal;

import org.fenixedu.bennu.cms.domain.Site;
import org.fenixedu.bennu.cms.routing.CMSURLHandler;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by nurv on 26/08/14.
 */
@Controller
public class DefaultSiteController {

    private CMSURLHandler handler;

    @Autowired
    public DefaultSiteController(CMSURLHandler handler) {
        this.handler = handler;
    }

    @RequestMapping("/")
    public void handleDefaultSite(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Site s = Bennu.getInstance().getDefaultSite();

        if (s != null){
            MenuFunctionality functionality = s.getFunctionality();
            handler.handleRequest(functionality, req, resp, null);
        }else{
            throw new UnsupportedOperationException();
        }
    }

}

