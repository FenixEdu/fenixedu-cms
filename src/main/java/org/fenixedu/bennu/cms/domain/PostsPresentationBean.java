package org.fenixedu.bennu.cms.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PostsPresentationBean {
    private final Set<Post> allPosts;

    public PostsPresentationBean(Set<Post> allPosts) {
        this.allPosts = allPosts;
    }

    public HashMap<String, Object> paginate(Page page, int currentPage, int postsPerPage) {
        List<Post> posts = getVisiblePosts();
        int pages = (posts.size() / postsPerPage) + 1;
        HashMap<String, Object> pagination = new HashMap<>();

        if (currentPage < 1) {
            currentPage = 1;
        }

        if (currentPage > pages) {
            currentPage = pages;
        }

        int fromIndex = (currentPage - 1) * postsPerPage;
        int toIndex = Math.min(fromIndex + postsPerPage, posts.size());

        List<Post> pagePosts = posts.subList(fromIndex, toIndex);
        List<Object> pageList = new ArrayList<>();

        for (int i = 0; i < pages; i++) {
            HashMap<String, Object> info = new HashMap<String, Object>();
            info.put("url", "?p=" + (i + 1));
            info.put("number", (i + 1));
            pageList.add(info);
        }

        pagination.put("pages", pageList);
        pagination.put("current", currentPage);
        pagination.put("posts", pagePosts);

        return pagination;
    }

    public List<Post> getVisiblePosts() {
        return allPosts.stream().filter(p -> p.getComponentSet().isEmpty() && p.isVisible())
                .sorted(Post.CREATION_DATE_COMPARATOR).collect(Collectors.toList());
    }

    public int currentPage(String currentPageString) {
        int currentPage = 1;
        if (currentPageString != null) {
            try {
                currentPage = new Integer(currentPageString);
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }
        return currentPage;
    }
}
