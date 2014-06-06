package org.fenixedu.bennu.cms.domain;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.cms.exceptions.ResourceNotFoundException;
import org.fenixedu.bennu.cms.rendering.TemplateContext;

/**
 * Component that obtains the necessary info about a {@link Post}
 */
@ComponentType(type = "viewPost", name = "View Post", description = "View a Single Post")
public class ViewPost extends ViewPost_Base {

    public ViewPost() {
        super();
    }

    /**
     * fetches a post based on the 'q' parameter of the request and saves that post on the local and global context as 'post'
     */
    @Override
    public void handle(Page page, HttpServletRequest req, TemplateContext local, TemplateContext global) {
        String post = req.getParameter("q");
        if (post != null){
            Post p = page.getSite().postForSlug(post);
            local.put("post", p);
            global.put("post", p);
        }else{
            throw new ResourceNotFoundException();
        }
    }
}
