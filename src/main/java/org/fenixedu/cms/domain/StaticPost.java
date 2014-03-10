package org.fenixedu.cms.domain;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.cms.rendering.TemplateContext;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@ComponentType(type="staticPost", name="Static Post", description="Static Post")
public class StaticPost extends StaticPost_Base {
    
    public StaticPost() {
        super();
    }

    @Override
    public void handle(Page page, HttpServletRequest req, TemplateContext local, TemplateContext global) {
        Post post = this.getPost();
        local.put("post", post);
        global.put("post", post);
    }
    
    @Override
    @Atomic(mode=TxMode.WRITE)
    public void delete() {
        this.setPost(null);
        super.delete();
    }
    
    @Override
    public String getName() {
        String name = super.getName();
        return name + " (" + getPost().getName().getContent() + ")";
    }
}
