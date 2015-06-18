package org.fenixedu.cms.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;

@RunWith(FenixFrameworkRunner.class)
public class TestSite extends TestCMS {

    @Test
    public void createSite() {
        User user = CmsTestUtils.createAuthenticatedUser("createSite");

        String methodName = "createSite";
        LocalizedString siteName =
                new LocalizedString(Locale.UK, "site name uk " + methodName).with(Locale.US, "site name us " + methodName);
        LocalizedString siteDescription =
                new LocalizedString(Locale.UK, "site description uk " + methodName).with(Locale.US, "site description us "
                        + methodName);

        Site site = new Site(siteName, siteDescription);
        DateTime dt = new DateTime();

        assertEquals(siteName, site.getName());
        assertEquals(siteDescription, site.getDescription());
        assertEquals(null, site.getAlternativeSite());
        assertEquals(null, site.getStyle());
        assertEquals("slug should be initialized using site name", StringNormalizer.slugify(siteName.getContent()),
                site.getSlug());
        assertEquals(false, site.getPublished());
        assertEquals(null, site.getAnalyticsCode());
        assertEquals(null, site.getThemeType());
        assertTrue(equalDates(dt, site.getCreationDate()));
        assertEquals(null, site.getTheme());
        assertTrue(site.getCategoriesSet().isEmpty());
        assertTrue(site.getMenusSet().isEmpty());
        assertTrue(site.getPostSet().isEmpty());
        assertTrue(site.getPagesSet().isEmpty());
        assertEquals(Bennu.getInstance(), site.getBennu());
        assertEquals(null, site.getFolder());
        assertEquals(null, site.getFunctionality());
        assertEquals(user, site.getCreatedBy());
    }

    @Test
    public void addSitePosts() {
        User user = CmsTestUtils.createAuthenticatedUser("addSitePosts");

        Site site = CmsTestUtils.createSite(user, "addSitePosts");
        DateTime dt = new DateTime();

        LocalizedString siteName = site.getName();
        LocalizedString siteDescription = site.getDescription();

        Post post1 = new Post(site);
        Post post2 = new Post(site);

        assertEquals(siteName, siteName);
        assertEquals(siteDescription, siteDescription);
        assertEquals(null, site.getAlternativeSite());
        assertEquals(null, site.getStyle());
        assertEquals(StringNormalizer.slugify(siteName.getContent()), site.getSlug());
        assertEquals(false, site.getPublished());
        assertEquals(null, site.getAnalyticsCode());
        assertEquals(null, site.getThemeType());
        assertTrue(equalDates(dt, site.getCreationDate()));
        assertEquals(null, site.getTheme());
        assertTrue(site.getCategoriesSet().isEmpty());
        assertTrue(site.getMenusSet().isEmpty());
        assertEquals(2, site.getPostSet().size());
        assertTrue(site.getPostSet().contains(post1) && site.getPostSet().contains(post2));
        assertTrue(site.getPagesSet().isEmpty());
        assertEquals(Bennu.getInstance(), site.getBennu());
        assertEquals(null, site.getFolder());
        assertEquals(null, site.getFunctionality());
        assertEquals(user, site.getCreatedBy());
    }

    @Test
    public void addSitePages() {
        User user = CmsTestUtils.createAuthenticatedUser("addSitePages");

        Site site = CmsTestUtils.createSite(user, "addSitePages");
        DateTime dt = new DateTime();

        LocalizedString siteName = site.getName();
        LocalizedString siteDescription = site.getDescription();

        Page page1 = new Page(site, CmsTestUtils.createLocalizedString("addSitePages1"));
        Page page2 = new Page(site, CmsTestUtils.createLocalizedString("addSitePages2"));

        assertEquals(siteName, siteName);
        assertEquals(siteDescription, siteDescription);
        assertEquals(null, site.getAlternativeSite());
        assertEquals(null, site.getStyle());
        assertEquals(StringNormalizer.slugify(siteName.getContent()), site.getSlug());
        assertEquals(false, site.getPublished());
        assertEquals(null, site.getAnalyticsCode());
        assertEquals(null, site.getThemeType());
        assertTrue(equalDates(dt, site.getCreationDate()));
        assertEquals(null, site.getTheme());
        assertTrue(site.getCategoriesSet().isEmpty());
        assertTrue(site.getMenusSet().isEmpty());
        assertTrue(site.getPostSet().isEmpty());
        assertEquals(2, site.getPagesSet().size());
        assertTrue(site.getPagesSet().contains(page1) && site.getPagesSet().contains(page2));
        assertEquals(Bennu.getInstance(), site.getBennu());
        assertEquals(null, site.getFolder());
        assertEquals(null, site.getFunctionality());
        assertEquals(user, site.getCreatedBy());
    }

    @Test
    public void addSiteCategories() {
        User user = CmsTestUtils.createAuthenticatedUser("addSiteCategories");

        Site site = CmsTestUtils.createSite(user, "addSiteCategories");
        DateTime dt = new DateTime();

        LocalizedString siteName = site.getName();
        LocalizedString siteDescription = site.getDescription();

        Category cat1 = new Category(site, CmsTestUtils.createLocalizedString("addSiteCategories1"));
        Category cat2 = new Category(site, CmsTestUtils.createLocalizedString("addSiteCategories1"));

        assertEquals(siteName, siteName);
        assertEquals(siteDescription, siteDescription);
        assertEquals(null, site.getAlternativeSite());
        assertEquals(null, site.getStyle());
        assertEquals(StringNormalizer.slugify(siteName.getContent()), site.getSlug());
        assertEquals(false, site.getPublished());
        assertEquals(null, site.getAnalyticsCode());
        assertEquals(null, site.getThemeType());
        assertTrue(equalDates(dt, site.getCreationDate()));
        assertEquals(null, site.getTheme());
        assertTrue(site.getPagesSet().isEmpty());
        assertTrue(site.getMenusSet().isEmpty());
        assertTrue(site.getPostSet().isEmpty());
        assertEquals(2, site.getCategoriesSet().size());
        assertTrue(site.getCategoriesSet().contains(cat1) && site.getCategoriesSet().contains(cat2));
        assertEquals(Bennu.getInstance(), site.getBennu());
        assertEquals(null, site.getFolder());
        assertEquals(null, site.getFunctionality());
        assertEquals(user, site.getCreatedBy());
    }

    @Test
    public void addSiteMenus() {
        User user = CmsTestUtils.createAuthenticatedUser("addSiteMenus");

        Site site = CmsTestUtils.createSite(user, "addSiteMenus");
        DateTime dt = new DateTime();

        LocalizedString siteName = site.getName();
        LocalizedString siteDescription = site.getDescription();

        Menu menu1 = new Menu(site, CmsTestUtils.createLocalizedString("addSiteMenus1"));
        Menu menu2 = new Menu(site, CmsTestUtils.createLocalizedString("addSiteMenus2"));

        assertEquals(siteName, siteName);
        assertEquals(siteDescription, siteDescription);
        assertEquals(null, site.getAlternativeSite());
        assertEquals(null, site.getStyle());
        assertEquals(StringNormalizer.slugify(siteName.getContent()), site.getSlug());
        assertEquals(false, site.getPublished());
        assertEquals(null, site.getAnalyticsCode());
        assertEquals(null, site.getThemeType());
        assertTrue(equalDates(dt, site.getCreationDate()));
        assertEquals(null, site.getTheme());
        assertTrue(site.getCategoriesSet().isEmpty());
        assertTrue(site.getPagesSet().isEmpty());
        assertTrue(site.getPostSet().isEmpty());
        assertEquals(2, site.getMenusSet().size());
        assertTrue(site.getMenusSet().contains(menu1) && site.getMenusSet().contains(menu2));
        assertEquals(Bennu.getInstance(), site.getBennu());
        assertEquals(null, site.getFolder());
        assertEquals(null, site.getFunctionality());
        assertEquals(user, site.getCreatedBy());
    }

    @Test
    public void getSiteFromSlug() {
        User user = CmsTestUtils.createAuthenticatedUser("getSiteFromSlug");

        Site site = CmsTestUtils.createSite(user, "getSiteFromSlug");
        Site slugSite = Site.fromSlug(site.getSlug());

        assertEquals(site, slugSite);
    }

    @Test
    public void getPageFromSlug() {
        User user = CmsTestUtils.createAuthenticatedUser("getPageFromSlug");

        Site site = CmsTestUtils.createSite(user, "getPageFromSlug");
        Page page = CmsTestUtils.createPage(site, "getPageFromSlug");

        Page slugPage = site.pageForSlug(page.getSlug());

        assertEquals(page, slugPage);
    }

    @Test
    public void getPostFromSlug() {
        User user = CmsTestUtils.createAuthenticatedUser("getPostFromSlug");

        Site site = CmsTestUtils.createSite(user, "getPostFromSlug");
        Post post = CmsTestUtils.createPost(site, "getPostFromSlug");

        Post slugPost = site.postForSlug(post.getSlug());

        assertEquals(post, slugPost);
    }

    @Test
    public void getCategoryFromSlug() {
        User user = CmsTestUtils.createAuthenticatedUser("getCategoryFromSlug");

        Site site = CmsTestUtils.createSite(user, "getCategoryFromSlug");
        Category category = CmsTestUtils.createCategory(site, "getCategoryFromSlug");

        Category slugCategory = site.categoryForSlug(category.getSlug());

        assertEquals(category, slugCategory);
    }

    @Test
    public void getUnexistentCategoryFromSlug() {
        User user = CmsTestUtils.createAuthenticatedUser("getUnexistentCategoryFromSlug");

        Site site = CmsTestUtils.createSite(user, "getUnexistentCategoryFromSlug");

        String slugRandom = "randomxptoslug";
        Category category1 =
                site.getOrCreateCategoryForSlug(slugRandom, CmsTestUtils.createLocalizedString("getUnexistentCategoryFromSlug"));

        assertTrue(category1 != null);
        assertEquals(slugRandom, category1.getSlug());

        String slugRandomError = "random slug";
        Category category2 =
                site.getOrCreateCategoryForSlug(slugRandomError,
                        CmsTestUtils.createLocalizedString("getUnexistentCategoryFromSlug"));

        assertTrue(category2 != null);
        assertEquals(StringNormalizer.slugify(slugRandomError), category2.getSlug());
    }
}
