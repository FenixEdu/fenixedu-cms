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

import org.fenixedu.cms.domain.CloneCache;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.rendering.TemplateContext;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import java.util.Collection;

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
    public StaticPost clone(CloneCache cloneCache) {
        return cloneCache.getOrClone(this, obj -> {
            StaticPost clone = new StaticPost(null);
            cloneCache.setClone(StaticPost.this, clone);
            clone.setPost(getPost().clone(cloneCache));
            return clone;
        });
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
