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

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.wraps.Wrap;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

public class PostsPresentationBean {
    private static final int FIRST_PAGE_INDEX = 1;
    private final Set<Post> allPosts;

    public PostsPresentationBean(Set<Post> allPosts) {
        this.allPosts = allPosts;
    }

    public HashMap<String, Object> paginate(Page page, int currentPage, int postsPerPage) {

        List<List<Wrap>> pages = Lists.partition(getVisiblePosts(), postsPerPage);
        currentPage = ensureRange(currentPage, FIRST_PAGE_INDEX, pages.size());
        List<Wrap> currentPagePosts = pages.isEmpty() ? Lists.newArrayList() : pages.get(currentPage - FIRST_PAGE_INDEX);

        HashMap<String, Object> pagination = Maps.newHashMap();
        pagination.put("pages", createPagesList(pages.size()));
        pagination.put("current", currentPage);
        pagination.put("posts", currentPagePosts);
        return pagination;
    }

    private List<Object> createPagesList(int numberOfPages) {
        return IntStream.rangeClosed(FIRST_PAGE_INDEX, numberOfPages)
                .mapToObj(i -> ImmutableMap.of("url", "?p=" + i, "number", i)).collect(toList());
    }

    private int ensureRange(int number, int min, int max) {
        return Math.max(Math.min(number, max), min);
    }

    public List<Wrap> getVisiblePosts() {
        return visiblePostsStream().collect(toList());
    }

    public List<Wrap> getVisiblePosts(long numPosts) {
        return visiblePostsStream().limit(numPosts).collect(toList());
    }

    private Stream<Wrap> visiblePostsStream() {
        return allPosts.stream().filter(p -> p.getComponentSet().isEmpty() && p.isVisible())
                .sorted(Post.CREATION_DATE_COMPARATOR).map(Wrap::make);

    }

    public int currentPage(String currentPageString) {
        boolean isValid = currentPageString != null && Ints.tryParse(currentPageString) != null;
        return isValid ? Ints.tryParse(currentPageString) : 1;
    }
}
