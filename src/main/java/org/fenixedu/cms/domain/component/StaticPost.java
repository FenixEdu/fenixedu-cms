package org.fenixedu.cms.domain.component;

import java.util.Collection;

import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.rendering.TemplateContext;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@ComponentType(name = "Static Post", description = "Static Post")
public class StaticPost extends StaticPost_Base {

    @DynamicComponent
    public StaticPost(@ComponentParameter(value = "Post", provider = PostsForSite.class) Post post) {
        super();
        setPost(post);
    }

    @Override
    public void handle(Page page, TemplateContext local, TemplateContext global) {
        Post post = this.getPost();
        local.put("post", post.makeWrap());
        global.put("post", post.makeWrap());
    }

    @Override
    @Atomic(mode = TxMode.WRITE)
    public void delete() {
        this.setPost(null);
        super.delete();
    }

    @Override
    public String getName() {
        String name = super.getName();
        return name + " (" + getPost().getName().getContent() + ")";
    }

    public Page getPage() {
        if (getInstalledPageSet().isEmpty()) {
            return null;
        } else {
            return getInstalledPageSet().iterator().next();
        }
    }

    public static class PostsForSite implements ComponentContextProvider<Post> {
        @Override
        public Collection<Post> provide(Page page) {
            return page.getSite().getPostSet();
        }

        @Override
        public String present(Post post) {
            return post.getName().getContent();
        }
    }

}
