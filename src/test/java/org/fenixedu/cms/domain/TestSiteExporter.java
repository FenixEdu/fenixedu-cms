package org.fenixedu.cms.domain;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.cms.domain.component.*;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;

import javax.servlet.ServletException;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

@RunWith(FenixFrameworkRunner.class)
public class TestSiteExporter extends TestCMS {

    @Test
    public void jsonExportEmptySite() {
        User user = CmsTestUtils.createAuthenticatedUser("exportEmptySiteUser");
        Site site = CmsTestUtils.createSite(user, "exportEmptySite");
        JsonObject json = new SiteExporter(site).export(site);
        assertTrue(json.has("slug"));
        assertEquals(json.get("slug").getAsString(), site.getSlug());

        assertTrue(json.has("name"));
        assertEquals(json.get("name"), site.getName().json());
        assertTrue(LocalizedString.fromJson(json.get("name")).equals(site.getName()));

        assertTrue(json.has("description"));
        assertEquals(json.get("description"), site.getDescription().json());
        assertTrue(LocalizedString.fromJson(json.get("description")).equals(site.getDescription()));

        assertTrue(json.has("themeType"));
        assertTrue(json.get("themeType").isJsonNull());

        assertTrue(json.has("embedded"));
        assertEquals(json.get("embedded").getAsBoolean(), site.getEmbedded());

        assertTrue(json.has("analyticsCode"));
        assertTrue(json.get("analyticsCode").isJsonNull());

        assertTrue(json.has("createdBy"));
        assertEquals(json.get("createdBy").getAsString(), site.getCreatedBy().getUsername());
        assertTrue(User.findByUsername(json.get("createdBy").getAsString()).equals(site.getCreatedBy()));

        assertTrue(json.has("published"));
        assertEquals(json.get("published").getAsBoolean(), site.getPublished());

        assertTrue(json.has("menus"));
        assertTrue(json.get("menus").isJsonArray());
        assertTrue(json.get("menus").getAsJsonArray().size() == site.getMenusSet().size());

        assertTrue(json.has("posts"));
        assertTrue(json.get("posts").isJsonArray());
        assertTrue(json.get("posts").getAsJsonArray().size() == site.getPostSet().size());

        assertTrue(json.has("pages"));
        assertTrue(json.get("pages").isJsonArray());
        assertTrue(json.get("pages").getAsJsonArray().size() == site.getPagesSet().size());

        assertTrue(json.has("categories"));
        assertTrue(json.get("categories").isJsonArray());
        assertTrue(json.get("categories").getAsJsonArray().size() == site.getCategoriesSet().size());
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

        JsonObject json = new SiteExporter(site).export(site);

        assertTrue(json.has("posts"));
        assertTrue(json.get("posts").isJsonArray());
        assertTrue(json.get("posts").getAsJsonArray().size() == 1);
        JsonElement postElement = json.get("posts").getAsJsonArray().get(0);
        assertNotNull(postElement);
        assertTrue(postElement.isJsonPrimitive());
        assertTrue(postElement.getAsString().equals(post.getSlug()));

        JsonObject postJson = new SiteExporter(site).export(post);

        assertTrue(postJson.has("slug"));
        assertEquals(postJson.get("slug").getAsString(), post.getSlug());

        assertTrue(postJson.has("site"));
        assertEquals(postJson.get("site").getAsString(), site.getSlug());

        assertTrue(postJson.has("name"));
        assertEquals(postJson.get("name"), post.getName().json());
        assertTrue(LocalizedString.fromJson(postJson.get("name").getAsJsonObject()).equals(post.getName()));

        assertTrue(postJson.has("body"));
        assertEquals(postJson.get("body"), post.getBody().json());
        assertTrue(LocalizedString.fromJson(postJson.get("body").getAsJsonObject()).equals(post.getBody()));

        assertTrue(postJson.has("createdBy"));
        assertEquals(postJson.get("createdBy").getAsString(), post.getCreatedBy().getUsername());
        assertTrue(User.findByUsername(postJson.get("createdBy").getAsString()).equals(post.getCreatedBy()));
        assertTrue(postJson.has("creationDate"));

        DateTime creationDate = ISODateTimeFormat.dateTime().parseDateTime(postJson.get("creationDate").getAsString());
        assertTrue(equalDates(creationDate, post.getCreationDate()));

        assertTrue(postJson.has("canViewGroup"));
        assertEquals(postJson.get("canViewGroup").getAsString(), post.getCanViewGroup().getExpression());
        assertTrue(Group.parse(postJson.get("canViewGroup").getAsString()).equals(post.getCanViewGroup()));

        assertTrue(postJson.has("active"));
        assertEquals(postJson.get("active").getAsBoolean(), post.getActive());

        assertTrue(postJson.has("location"));
        assertTrue(post.getLocation().equals(LocalizedString.fromJson(postJson.get("location"))));

        assertTrue(postJson.has("metadata"));
        assertTrue(postJson.get("metadata").isJsonObject());
        assertTrue(post.getMetadata().equals(new PostMetadata(postJson.get("metadata").getAsJsonObject())));

        assertTrue(postJson.has("modificationDate"));
        DateTime modificationDate = DateTime.parse(postJson.get("modificationDate").getAsString());
        assertTrue(equalDates(modificationDate, post.getModificationDate()));


        assertTrue(postJson.has("publicationBegin"));
        DateTime publicationBegin = DateTime.parse(postJson.get("publicationBegin").getAsString());
        assertTrue(equalDates(publicationBegin, post.getPublicationBegin()));

        assertTrue(postJson.has("publicationEnd"));
        assertTrue(postJson.get("publicationEnd").isJsonNull());

        assertTrue(postJson.has("files"));
        assertTrue(postJson.get("files").isJsonArray());
        assertTrue(postJson.get("files").getAsJsonArray().size() == post.getFilesSet().size());

        assertTrue(postJson.has("categories"));
        assertTrue(postJson.get("categories").isJsonArray());
        assertTrue(postJson.get("categories").getAsJsonArray().size() == post.getCategoriesSet().size());

    }

    @Test
    public void jsonCategories() {
        User user = CmsTestUtils.createAuthenticatedUser("jsonCategories");
        Site site = CmsTestUtils.createSite(user, "jsonCategories");
        Post post = CmsTestUtils.createPost(site, "jsonCategories");
        Category category1 = CmsTestUtils.createCategory(site, "jsonCategories1");
        Category category2 = CmsTestUtils.createCategory(site, "jsonCategories2");
        Category category3 = CmsTestUtils.createCategory(site, "jsonCategories3");
        category3.addComponents(new ListCategoryPosts(category3));

        post.addCategories(category1);
        post.addCategories(category2);
        post.addCategories(category3);

        JsonObject siteJson = new SiteExporter(site).export(site);
        assertTrue(siteJson.has("categories"));
        assertTrue(siteJson.get("categories").isJsonArray());
        assertEquals(siteJson.get("categories").getAsJsonArray().size(), site.getCategoriesSet().size());

        List<String> siteCatSlugs = site.getCategoriesSet().stream().map(Category::getSlug).collect(toList());
        for (JsonElement catEl : siteJson.get("categories").getAsJsonArray()) {
            assertTrue(catEl.isJsonPrimitive());
            assertTrue(siteCatSlugs.contains(catEl.getAsString()));
        }

        JsonObject postJson = new SiteExporter(site).export(post);
        assertTrue(postJson.has("categories"));
        assertTrue(postJson.get("categories").isJsonArray());
        assertTrue(postJson.get("categories").getAsJsonArray().size() == post.getCategoriesSet().size());

        List<String> postCatSlugs = post.getCategoriesSet().stream().map(Category::getSlug).collect(toList());
        for (JsonElement catEl : postJson.get("categories").getAsJsonArray()) {
            assertTrue(catEl.isJsonPrimitive());
            assertTrue(postCatSlugs.contains(catEl.getAsString()));
        }

        for (Category category : post.getCategoriesSet()) {
            JsonObject categoryJson = new SiteExporter(site).export(category);
            assertTrue(categoryJson.has("slug"));
            assertEquals(categoryJson.get("slug").getAsString(), category.getSlug());
            assertTrue(categoryJson.has("site"));
            assertEquals(categoryJson.get("site").getAsString(), site.getSlug());
            assertTrue(categoryJson.has("creationDate"));
            DateTime creationDate = DateTime.parse(categoryJson.get("creationDate").getAsString());
            assertTrue(equalDates(creationDate, category.getCreationDate()));
            assertTrue(categoryJson.has("createdBy"));
            assertEquals(User.findByUsername(categoryJson.get("createdBy").getAsString()), category.getCreatedBy());
            assertTrue(categoryJson.has("name"));
            assertEquals(LocalizedString.fromJson(categoryJson.get("name")), category.getName());
            assertTrue(categoryJson.has("posts"));
            assertTrue(categoryJson.get("posts").isJsonArray());
            assertTrue(categoryJson.get("posts").getAsJsonArray().size() == category.getPostsSet().size());
            List<String> categoryPosts = category.getPostsSet().stream().map(Post::getSlug).collect(toList());
            for (JsonElement postSlugEl : categoryJson.get("posts").getAsJsonArray()) {
                categoryPosts.contains(postSlugEl.getAsString());
            }
            assertTrue(categoryJson.has("components"));
            assertTrue(categoryJson.get("components").isJsonArray());
            assertTrue(categoryJson.get("components").getAsJsonArray().size() == category.getComponentsSet().size());
            for (JsonElement categoryComponentEl : categoryJson.get("components").getAsJsonArray()) {
                assertTrue(categoryComponentEl.isJsonObject());
                JsonObject categoryComponentJson = categoryComponentEl.getAsJsonObject();
                assertTrue(categoryComponentJson.has("type"));
                assertEquals(categoryComponentJson.get("type").getAsString(), ListCategoryPosts.class.getName());
                assertEquals(categoryComponentJson.get("site").getAsString(), category.getSite().getSlug());
                assertEquals(categoryComponentJson.get("category").getAsString(), category.getSlug());
            }
        }
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

        JsonObject siteJson = new SiteExporter(site).export(site);
        assertTrue(siteJson.has("pages"));
        assertTrue(siteJson.get("pages").isJsonArray());
        assertTrue(siteJson.get("pages").getAsJsonArray().size() == site.getPagesSet().size());
        List<String> sitePagesSlugs = site.getPagesSet().stream().map(Page::getSlug).collect(toList());
        for (JsonElement pageEl : siteJson.get("pages").getAsJsonArray()) {
            assertTrue(sitePagesSlugs.contains(pageEl.getAsString()));
        }

        for (Page page : site.getPagesSet()) {
            JsonObject pageJson = new SiteExporter(site).export(page);

            assertTrue(pageJson.has("slug"));
            assertEquals(pageJson.get("slug").getAsString(), page.getSlug());

            assertTrue(pageJson.has("name"));
            assertEquals(LocalizedString.fromJson(pageJson.get("name")), page.getName());

            assertTrue(pageJson.has("site"));
            assertEquals(pageJson.get("site").getAsString(), page.getSite().getSlug());

            assertTrue(pageJson.has("canViewGroup"));
            assertEquals(Group.parse(pageJson.get("canViewGroup").getAsString()), page.getCanViewGroup());

            assertTrue(pageJson.has("templateType"));
            if (page.getTemplateType() == null) {
                assertTrue(pageJson.get("templateType").isJsonNull());
            } else {
                assertEquals(pageJson.get("templateType").getAsString(), page.getTemplateType());
            }
            
            assertTrue(pageJson.has("published"));
            assertEquals(pageJson.get("published").getAsBoolean(), page.getPublished());

            assertTrue(pageJson.has("createdBy"));
            assertEquals(User.findByUsername(pageJson.get("createdBy").getAsString()), page.getCreatedBy());

            assertTrue(pageJson.has("creationDate"));
            DateTime creationDate = DateTime.parse(pageJson.get("creationDate").getAsString());
            assertTrue(equalDates(creationDate, page.getCreationDate()));

            assertTrue(pageJson.has("modificationDate"));
            DateTime modificationDate = DateTime.parse(pageJson.get("modificationDate").getAsString());
            assertTrue(equalDates(modificationDate, page.getModificationDate()));


            assertTrue(pageJson.has("menuItems"));
            assertTrue(pageJson.get("menuItems").isJsonArray());
            List<String> menuItemsIds = page.getMenuItemsSet().stream().map(MenuItem::getExternalId).collect(toList());
            assertEquals(pageJson.get("menuItems").getAsJsonArray().size(), page.getMenuItemsSet().size());
            for (JsonElement menuItemEl : pageJson.get("menuItems").getAsJsonArray()) {
                assertTrue(menuItemEl.isJsonPrimitive());
                assertTrue(menuItemsIds.contains(menuItemEl.getAsString()));
            }

            assertTrue(pageJson.has("components"));
            assertTrue(pageJson.get("components").isJsonArray());
            assertEquals(pageJson.get("components").getAsJsonArray().size(), page.getComponentsSet().size());
            List<String> pageComponentSlugs = page.getComponentsSet().stream().map(Component::getType).collect(toList());
            for (JsonElement componentEl : pageJson.get("components").getAsJsonArray()) {
                assertTrue(componentEl.isJsonObject());
                JsonObject componentJson = componentEl.getAsJsonObject();

                assertTrue(componentJson.has("type"));
                assertTrue(componentJson.get("type").isJsonPrimitive());
                assertTrue(pageComponentSlugs.contains(componentJson.get("type").getAsString()));

                ComponentDescriptor type = Component.forType(componentJson.get("type").getAsString());
                assertNotNull(type);
                if (!type.isStateless()) {
                    assertTrue(componentJson.has("site"));
                    assertEquals(componentJson.get("site").getAsString(), page.getSite().getSlug());

                    assertTrue(componentJson.has("page"));
                    assertEquals(componentJson.get("page").getAsString(), page.getSlug());
                }
            }
        }
    }

}
