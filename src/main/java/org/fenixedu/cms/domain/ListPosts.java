package org.fenixedu.cms.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.cms.rendering.TemplateContext;

/**
 * Component that lists all the non-static {@link Post}
 */
@ComponentType(type = "listPost", name = "List Posts", description = "List all non-static Posts")
public class ListPosts extends ListPosts_Base {

    public static final int POSTS_PER_PAGE = 5;

    public ListPosts() {
        super();
    }

    @Override
    public void handle(Page page, HttpServletRequest req, TemplateContext local, TemplateContext global) {
        List<Post> posts = new ArrayList<>();

        for (Post p : page.getSite().getPostSet()) {
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

        int pages = (posts.size() / POSTS_PER_PAGE) + 1;
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

        int fromIndex = (currentPage - 1) * POSTS_PER_PAGE;
        int toIndex = Math.min(fromIndex + POSTS_PER_PAGE, posts.size());

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
}
