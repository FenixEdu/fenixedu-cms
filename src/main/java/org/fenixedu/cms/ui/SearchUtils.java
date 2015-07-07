package org.fenixedu.cms.ui;

import com.google.common.base.Strings;
import com.google.common.math.IntMath;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.PostFile;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;

import java.math.RoundingMode;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Created by borgez-dsi on 19-05-2015.
 */
public class SearchUtils {

    public static List<Site> searchSites(Collection<Site> allSites, String query) {
        return allSites.stream().filter(site -> matches(site, query)).sorted(Site.NAME_COMPARATOR).collect(toList());
    }

    public static List<Post> searchPosts(Collection<Post> allPosts, String query) {
        return allPosts.stream().filter(post -> matches(post, query)).collect(toList());
    }

    public static List<PostFile> searchFiles(Collection<PostFile> allPostFiles, String query) {
        return allPostFiles.stream().filter(post -> matches(post, query)).collect(toList());
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

    private static boolean matches(PostFile postFile, String query) {
        return postFile.getFiles().getDisplayName().contains(query)
                || postFile.getFiles().getFilename().contains(query) || matches(postFile.getPost(), query);
    }

    public static class Partition <T> {
        private final List<T> partitionItems;
        private final int currentPartitionNumber;
        private final int itemsPerPartition;
        private final int numPartitions;

        public Partition(Collection<T> allItems, Comparator<T> comparator, int itemsPerPartition, int currentPartition) {
            this.itemsPerPartition = itemsPerPartition;
            this.numPartitions = IntMath.divide(allItems.size(), itemsPerPartition, RoundingMode.CEILING);
            this.currentPartitionNumber = Math.min(this.numPartitions, Math.max(1, currentPartition));
            this.partitionItems = allItems.stream().sorted(comparator)
                            .skip((currentPartition - 1) * itemsPerPartition).limit(itemsPerPartition).collect(toList());
        }

        public List<T> getItems() {
            return this.partitionItems;
        }

        public int getItemsPerPartition() {
            return itemsPerPartition;
        }

        public int getNumber() {
            return currentPartitionNumber;
        }

        public int getNumPartitions() {
            return this.numPartitions;
        }

        public boolean isFirst() {
            return getNumber() == 1;
        }

        public boolean isLast() {
            return getNumber() == getNumPartitions();
        }

    }
}
