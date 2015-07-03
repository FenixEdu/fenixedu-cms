package org.fenixedu.cms.domain;

import com.google.common.io.Files;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.component.ListCategoryPosts;
import org.fenixedu.cms.domain.component.StaticPost;
import org.fenixedu.cms.domain.component.ViewPost;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;

import javax.servlet.ServletException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipFile;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

@RunWith(FenixFrameworkRunner.class)
public class TestSiteImportExportIntegration extends TestCMS {

    @Test
    public void jsonImportEmptySite() {
        User user = CmsTestUtils.createAuthenticatedUser("exportEmptySiteUser");
        Site site = CmsTestUtils.createSite(user, "exportEmptySite");
        Site importedSite = exportAndImport(site);
        assertEqualSites(site, importedSite);
    }

    @Test
    public void jsonExportPost() {
        User user = CmsTestUtils.createAuthenticatedUser("jsonExportEmptyPost");
        Site site = CmsTestUtils.createSite(user, "jsonExportEmptyPost");
        Post post = CmsTestUtils.createPost(site, "jsonExportEmptyPost");
        PostContentRevision version = CmsTestUtils.createVersion(post, "jsonExportEmptyPostPostBody");
        post.setLocation(CmsTestUtils.createLocalizedString("jsonExportEmptyPostLocation"));
        post.setMetadata(new PostMetadata().with("firstKey", "my-test-value").with("secondKey", "secondTestValue"));
        post.setPublicationBegin(new DateTime());
        post.setPublicationEnd(null);
        Site importedSite = exportAndImport(site);
        assertEqualSites(site, importedSite);
    }

    @Test
    public void jsonCategories() {
        User user = CmsTestUtils.createAuthenticatedUser("jsonCategories");
        Site site = CmsTestUtils.createSite(user, "jsonCategories");
        Post post = CmsTestUtils.createPost(site, "jsonCategories");
        Category category1 = CmsTestUtils.createCategory(site, "jsonCategories1");
        Category category2 = CmsTestUtils.createCategory(site, "jsonCategories2");
        Category category3 = CmsTestUtils.createCategory(site, "jsonCategories3");
        category2.addComponents(new ListCategoryPosts(category2));

        category3.addComponents(new ListCategoryPosts(category3));
        category3.addComponents(new ListCategoryPosts(category3));
        category3.addComponents(new ListCategoryPosts(category3));

        Site importedSite = exportAndImport(site);
        assertEqualSites(site, importedSite);
    }

    @Test
    public void jsonPage() throws ServletException {
        User user = CmsTestUtils.createAuthenticatedUser("jsonPage");
        Site site = CmsTestUtils.createSite(user, "jsonPage");
        Page page1 = CmsTestUtils.createPage(site, "jsonPage1");
        Page page2 = CmsTestUtils.createPage(site, "jsonPage2");
        Page page3 = CmsTestUtils.createPage(site, "jsonPage3");
        page2.addComponents(new ListCategoryPosts(CmsTestUtils.createCategory(site, "jsonPage")));
        page3.addComponents(new StaticPost(CmsTestUtils.createPost(site, "jsonPage")));
        page3.addComponents(new StaticPost(CmsTestUtils.createPost(site, "jsonPage22")));
        page3.addComponents(new ListCategoryPosts(CmsTestUtils.createCategory(site, "jsonPage22Cate")));
        page3.addComponents(Component.forType(ViewPost.class));

        Site importedSite = exportAndImport(site);
        assertEqualSites(site, importedSite);
    }

    private void assertEqualSites(Site site, Site importedSite) {
        assertNotNull(importedSite);
        Assert.assertEquals(importedSite.getName(), site.getName());
        Assert.assertEquals(importedSite.getCanAdminGroup(), site.getCanAdminGroup());
        Assert.assertEquals(importedSite.getCanViewGroup(), site.getCanViewGroup());
        Assert.assertEquals(importedSite.getCanPostGroup(), site.getCanPostGroup());
        Assert.assertNotEquals(importedSite.getExternalId(), site.getExternalId());
        Assert.assertEquals(importedSite.getThemeType(), site.getThemeType());
        Assert.assertFalse(importedSite.getPublished());
        Assert.assertEquals(importedSite.getDescription(), site.getDescription());
        Assert.assertTrue(importedSite.getInitialPage() == null && site.getInitialPage() == null
                || importedSite.getInitialPage().getSlug().equals(site.getInitialPage().getSlug()));
        Assert.assertEquals(importedSite.getEmbedded(), site.getEmbedded());
        Assert.assertEquals(importedSite.getAnalyticsCode(), site.getAnalyticsCode());


        Assert.assertEquals(importedSite.getCategoriesSet().size(), site.getCategoriesSet().size());
        for (Category originalCategory : site.getCategoriesSet()) {
            Category importedCategory = importedSite.categoryForSlug(originalCategory.getSlug());
            assertNotNull(importedCategory);
            Assert.assertEquals(importedCategory.getName(), originalCategory.getName());
            Assert.assertEquals(importedCategory.getPostsSet().size(), originalCategory.getPostsSet().size());
            assertTrue(importedCategory.getComponentsSet().stream().map(Component::getType).collect(toList())
                    .containsAll(originalCategory.getComponentsSet().stream().map(Component::getType).collect(toList())));

            assertTrue(importedCategory.getPostsSet().stream().map(Post::getSlug).collect(toList())
                    .containsAll(originalCategory.getPostsSet().stream().map(Post::getSlug).collect(toList())));
        }

        Assert.assertEquals(importedSite.getPagesSet().size(), site.getPagesSet().size());
        for (Page originalPage : site.getPagesSet()) {
            Page importedPage = importedSite.pageForSlug(originalPage.getSlug());
            assertNotNull(importedPage);
            Assert.assertEquals(importedPage.getName(), originalPage.getName());
            Assert.assertEquals(importedPage.getComponentsSet().size(), originalPage.getComponentsSet().size());
            assertTrue(importedPage.getComponentsSet().stream().map(Component::getType).collect(toList())
                    .containsAll(originalPage.getComponentsSet().stream().map(Component::getType).collect(toList())));
        }

        Assert.assertEquals(importedSite.getMenusSet().size(), site.getMenusSet().size());
        for (Menu originalMenu : site.getMenusSet()) {
            Menu importedMenu = site.menuForSlug(originalMenu.getSlug());
            assertNotNull(importedMenu);
            Assert.assertEquals(importedMenu.getName(), originalMenu.getName());
            Assert.assertEquals(importedMenu.getToplevelItemsSet().size(), originalMenu.getToplevelItemsSet().size());

            Assert.assertEquals(importedMenu.getItemsSet().size(), originalMenu.getItemsSet().size());
            List<MenuItem> originalMenuItems = originalMenu.getItemsSorted().collect(toList());
            List<MenuItem> importedMenuItems = importedMenu.getItemsSorted().collect(toList());
            for (int i = 0; i < originalMenuItems.size(); ++i) {
                MenuItem originalMenuItem = originalMenuItems.get(i);
                MenuItem importedMenuItem = importedMenuItems.get(i);

                Assert.assertEquals(importedMenuItem.getName(), originalMenuItem.getName());
                Assert.assertEquals(importedMenuItem.getAddress(), originalMenuItem.getAddress());
                Assert.assertEquals(importedMenuItem.getMenu().getSlug(), originalMenuItem.getMenu().getSlug());
                Assert.assertEquals(importedMenuItem.getPosition(), originalMenuItem.getPosition());
                Assert.assertEquals(importedMenuItem.getChildrenSet().size(), originalMenuItem.getChildrenSet().size());
                Assert.assertEquals(importedMenuItem.getFolder(), originalMenuItem.getFolder());
                Assert.assertEquals(importedMenuItem.getUrl(), originalMenuItem.getUrl());
                Assert.assertTrue(importedMenuItem.getTop() == null && originalMenuItem.getTop() == null
                        || importedMenuItem.getTop().getSlug().equals(originalMenuItem.getTop().getSlug()));

                Assert.assertTrue(importedMenuItem.getPage() == null && originalMenuItem.getPage() == null
                        || importedMenuItem.getPage().getSlug().equals(originalMenuItem.getPage().getSlug()));
            }

        }

        Assert.assertEquals(importedSite.getPostSet().size(), site.getPostSet().size());
        for (Post originalPost : site.getPostSet()) {
            Post importedPost = importedSite.postForSlug(originalPost.getSlug());

            assertNotNull(importedPost);
            assertEquals(importedPost.getName(), originalPost.getName());
            assertEquals(importedPost.getBody(), originalPost.getBody());
            assertEquals(importedPost.getCanViewGroup(), originalPost.getCanViewGroup());
            assertEquals(importedPost.getCategoriesSet().size(), originalPost.getCategoriesSet().size());
            importedPost.getCategoriesSet().stream().map(Category::getSlug).collect(toList())
                    .containsAll(originalPost.getCategoriesSet().stream().map(Category::getSlug).collect(toList()));

            assertEquals(importedPost.getMetadata(), originalPost.getMetadata());
            assertTrue(equalDates(importedPost.getPublicationBegin(), originalPost.getPublicationBegin()));
            assertTrue(equalDates(importedPost.getPublicationEnd(), originalPost.getPublicationEnd()));
            assertEquals(importedPost.getLocation(), originalPost.getLocation());
            assertEquals(importedPost.getActive(), originalPost.getActive());
            List<PostFile> originalPostFiles = originalPost.getFilesSorted();
            List<PostFile> importedPostFiles = importedPost.getFilesSorted();
            for (int i = 0; i < originalPostFiles.size(); ++i) {
                PostFile originalPostFile = originalPostFiles.get(i);
                PostFile importedPostFile = importedPostFiles.get(i);
                assertEquals(originalPostFile.getIndex(), importedPostFile.getIndex());
                assertEquals(originalPostFile.getIsEmbedded(), importedPostFile.getIsEmbedded());
                assertEquals(originalPostFile.getFiles().getDisplayName(), importedPostFile.getFiles().getDisplayName());
                assertEquals(originalPostFile.getFiles().getFilename(), originalPostFile.getFiles().getFilename());
                assertEquals(originalPostFile.getFiles().getSize(), importedPostFile.getFiles().getSize());
                assertEquals(originalPostFile.getFiles().getChecksum(), importedPostFile.getFiles().getChecksum());
            }

        }
    }

    private Site exportAndImport(Site site) {
        try {
            ByteArrayOutputStream exportedSite = new SiteExporter(site).export();
            File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".zip");
            Files.write(exportedSite.toByteArray(), tempFile);
            return new SiteImporter(new ZipFile(tempFile)).importSite();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            return null;
        }
    }

}
