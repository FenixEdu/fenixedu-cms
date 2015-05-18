package org.fenixedu.cms.ui;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.common.base.Strings;

/**
 * Created by borgez-dsi on 19-05-2015.
 */
public class SearchUtils {

    public static List<Site> searchSites(Collection<Site> allSites, String query) {
        return allSites.stream().filter(site -> matches(site, query)).collect(Collectors.toList());
    }

    public static List<Post> searchPosts(Collection<Post> allPosts, String query) {
        return allPosts.stream().filter(post -> matches(post, query)).collect(Collectors.toList());
    }

    public static Collection<Page> searchPages(Collection<Page> allPages, String query) {
        return allPages.stream().filter(page -> matches(page, query)).collect(Collectors.toSet());
    }

    private static boolean matches(Post post, String query) {
        return containsContent(post.getName(), query) || containsContent(post.getBody(), query)
                || post.getCategoriesSet().stream().filter(cat -> matches(cat, query)).findAny().isPresent();
    }

    private static boolean matches(Page page, String query) {
        return containsContent(page.getName(), query) || containsContent(page.getSlug(), query)
                || containsContent(page.getAddress(), query) || containsContent(page.getCreatedBy(), query);
    }

    private static boolean matches(Category category, String query) {
        return containsContent(category.getName(), query);
    }

    private static boolean containsContent(LocalizedString localized, String query) {
        return localized != null && !localized.isEmpty()
                && contentFor(localized).filter(content -> containsContent(content, query)).findAny().isPresent();
    }

    private static boolean containsContent(String str, String query) {
        return !Strings.isNullOrEmpty(str) && str.toLowerCase().contains(query);
    }

    private static Stream<String> contentFor(LocalizedString localized) {
        return localized.getLocales().stream().map(locale -> localized.getContent(locale))
                .filter(content -> !Strings.isNullOrEmpty(content));
    }

    private static boolean matches(Site site, String query) {
        return containsContent(site.getName(), query) || containsContent(site.getDescription(), query)
                || containsContent(site.getSlug(), query) || containsContent(site.getCreatedBy(), query);
    }

    private static boolean containsContent(User user, String query) {
        return user != null && user.getProfile() != null && containsContent(user.getProfile().getFullName(), query);
    }
}
