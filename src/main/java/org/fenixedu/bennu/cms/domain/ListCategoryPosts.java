package org.fenixedu.bennu.cms.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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

    public ListCategoryPosts() {
    }

    public ListCategoryPosts(Category category) {
        this.setCategory(category);
    }

    @Override
    public void handle(Page page, HttpServletRequest req, TemplateContext local, TemplateContext global) {
        Category category = Optional.of(getCategory()).orElseGet(() -> page.getSite().categoryForSlug(req.getParameter("c")));
        local.put("category", category);        
        global.put("category", category);
        
        List<Post> posts = new ArrayList<>();

        for (Post p : category.getPostsSet()) {
            if (p.getComponentSet().size() == 0) {
                posts.add(p);
            }
        }

        Collections.sort(posts, new Comparator<Post>() {

            @Override
            public int compare(Post o1, Post o2) {
                return o1.getCreationDate().compareTo(o2.getCreationDate());
            }
        });

        int pages = (posts.size() / ListPosts.POSTS_PER_PAGE) + 1;
        HashMap<String, Object> pagination = new HashMap<>();

        String currentPageString = req.getParameter("p");
        int currentPage = 1;
        if (currentPageString != null) {
            try {
                currentPage = new Integer(currentPageString);
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        if (currentPage < 1) {
            currentPage = 1;
        }

        if (currentPage > pages) {
            currentPage = pages;
        }

        int fromIndex = (currentPage - 1) * ListPosts.POSTS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ListPosts.POSTS_PER_PAGE, posts.size());

        List<Post> pagePosts = posts.subList(fromIndex, toIndex);
        List<Object> pageList = new ArrayList<>();
        
        for (int i = 0; i < pages; i++) {
            HashMap<String, Object> info = new HashMap<String, Object>();
            info.put("url", page.getAddress() + "?p=" + (i + 1));
            info.put("number", (i + 1));
            pageList.add(info);
        }
        
        pagination.put("pages", pageList);
        pagination.put("current", currentPage);
        pagination.put("posts", pagePosts);

        local.put("posts", posts);
        local.put("pagination", pagination);

        global.put("posts", posts);
        global.put("pagination", pagination);
    }

    @Override
    @Atomic(mode=TxMode.WRITE)
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
