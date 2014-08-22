package org.fenixedu.bennu.cms.domain;

import java.util.HashMap;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.cms.rendering.TemplateContext;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

/**
 * Component that lists the {@link Post} of a given category.
 */
@ComponentType(type = "listCategoryPosts", name = "List Category Posts", description = "Lists the Posts from a given category")
public class ListCategoryPosts extends ListCategoryPosts_Base {

    private static final int POSTS_PER_PAGE = 5;

    public ListCategoryPosts(Category category) {
        this.setCategory(category);
    }

    @Override
    public void handle(Page page, HttpServletRequest req, TemplateContext local, TemplateContext global) {
        Category category = Optional.of(getCategory()).orElseGet(() -> page.getSite().categoryForSlug(req.getParameter("c")));
        local.put("category", category);
        global.put("category", category);

        PostsPresentationBean postsPresentation = new PostsPresentationBean(category.getPostsSet());
        int currentPage = postsPresentation.currentPage(req.getParameter("p"));
        HashMap<String, Object> pagination = postsPresentation.paginate(page, currentPage, POSTS_PER_PAGE);

        local.put("posts", postsPresentation.getVisiblePosts());
        local.put("pagination", pagination);

        global.put("posts", postsPresentation.getVisiblePosts());
        global.put("pagination", pagination);
    }

    @Override
    @Atomic(mode = TxMode.WRITE)
    public void delete() {
        this.setCategory(null);
        super.delete();
    }

    @Override
    public String getName() {
        String name = super.getName();
        if (getCategory() != null) {
            return name + " (" + getCategory().getName().getContent() + ")";
        } else {
            return name;
        }
    }

}
