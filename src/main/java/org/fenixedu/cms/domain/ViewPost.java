package org.fenixedu.cms.domain;

import java.io.FileNotFoundException;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.cms.exceptions.ResourceNotFoundException;
import org.fenixedu.cms.rendering.TemplateContext;

@ComponentType(type = "viewPost", name = "View Post", description = "View a Single Post")
public class ViewPost extends ViewPost_Base {

    public ViewPost() {
        super();
    }

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
