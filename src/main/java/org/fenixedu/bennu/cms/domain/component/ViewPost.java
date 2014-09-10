package org.fenixedu.bennu.cms.domain.component;

import org.fenixedu.bennu.cms.domain.Page;
import org.fenixedu.bennu.cms.domain.Post;
import org.fenixedu.bennu.cms.exceptions.ResourceNotFoundException;
import org.fenixedu.bennu.cms.rendering.TemplateContext;

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
            local.put("post", p.makeWrap());
            global.put("post", p.makeWrap());
        } else {
            throw new ResourceNotFoundException();
        }
    }
}
