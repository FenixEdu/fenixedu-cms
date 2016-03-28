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
package org.fenixedu.cms.ui;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.routing.CMSURLHandler;
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
