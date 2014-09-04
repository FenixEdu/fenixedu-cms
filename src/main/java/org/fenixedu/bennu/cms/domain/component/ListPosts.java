package org.fenixedu.bennu.cms.domain.component;

import java.util.HashMap;

import org.fenixedu.bennu.cms.domain.Page;
import org.fenixedu.bennu.cms.domain.Post;
import org.fenixedu.bennu.cms.domain.PostsPresentationBean;
import org.fenixedu.bennu.cms.rendering.TemplateContext;

/**
 * Component that lists all the non-static {@link Post}
 */
@ComponentType(type = "listPost", name = "List Posts", description = "List all non-static Posts")
public class ListPosts implements CMSComponent {

    public static final int POSTS_PER_PAGE = 5;

    @Override
    public void handle(Page page, TemplateContext local, TemplateContext global) {
        PostsPresentationBean postsPresentation = new PostsPresentationBean(page.getSite().getPostSet());
        int currentPage = postsPresentation.currentPage(global.getParameter("p"));
        HashMap<String, Object> pagination = postsPresentation.paginate(page, currentPage, POSTS_PER_PAGE);

        local.put("posts", postsPresentation.getVisiblePosts());
        local.put("pagination", pagination);

        global.put("posts", postsPresentation.getVisiblePosts());
        global.put("pagination", pagination);
    }

}
