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
package org.fenixedu.cms.domain.component;

import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.exceptions.ResourceNotFoundException;
import org.fenixedu.cms.rendering.TemplateContext;

/**
 * Component that obtains the necessary info about a {@link Post}
 */
@ComponentType(name = "View Post", description = "View a Single Post")
public class ViewPost implements CMSComponent {

    /**
     * fetches a post based on the 'q' parameter of the request and saves that post on the local and global context as 'post'
     */
    @Override
    public void handle(Page page, TemplateContext local, TemplateContext global) {
        String[] ctx = global.getRequestContext();
        if (ctx.length > 1) {
            Post p = page.getSite().postForSlug(ctx[1]);

            if (p == null || !p.getActive()) {
                throw new ResourceNotFoundException();
            }

            local.put("post", p.makeWrap());
            global.put("post", p.makeWrap());

        } else {
            throw new ResourceNotFoundException();
        }
    }
}
