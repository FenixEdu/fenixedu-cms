package org.fenixedu.bennu.cms.domain.component;

import org.fenixedu.bennu.cms.domain.Category;
import org.fenixedu.bennu.cms.domain.Page;
import org.fenixedu.bennu.cms.domain.Post;
import org.fenixedu.bennu.cms.rendering.TemplateContext;
import org.fenixedu.bennu.cms.domain.wraps.Wrap;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Component that lists the {@link Post} of a given category.
 */
@ComponentType(name = "List Category Posts", description = "Lists the Posts from a given category")
public class ListCategoryPosts extends ListCategoryPosts_Base {

    private static final int POSTS_PER_PAGE = 5;

    @DynamicComponent
    public ListCategoryPosts(
            @ComponentParameter(provider = CategoriesForSite.class, value = "Category", required = false) Category cat) {
        setCategory(cat);
    }

    @Override
    public void handle(Page page, TemplateContext local, TemplateContext global) {
        String slug = global.getRequestContext().length > 1 ? global.getRequestContext()[1] : null;
        Category category = getCategory() != null ? getCategory() : page.getSite().categoryForSlug(slug);
        local.put("category", category.makeWrap());
        global.put("category", category.makeWrap());

        PostsPresentationBean postsPresentation = new PostsPresentationBean(category.getPostsSet());
        int currentPage = postsPresentation.currentPage(global.getParameter("p"));
        HashMap<String, Object> pagination = postsPresentation.paginate(page, currentPage, POSTS_PER_PAGE);
        List<Wrap> posts = postsPresentation.getVisiblePosts().stream().collect(Collectors.toList());
        local.put("posts", posts);
        local.put("pagination", pagination);

        global.put("posts", posts);
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

    public Page getPage() {
        if (getInstalledPageSet().isEmpty()) {
            return null;
        } else {
            return getInstalledPageSet().iterator().next();
        }
    }

    public static class CategoriesForSite implements ComponentContextProvider<Category> {
        @Override
        public Collection<Category> provide(Page page) {
            return page.getSite().getCategoriesSet();
        }

        @Override
        public String present(Category category) {
            return category.getName().getContent();
        }
    }

}
