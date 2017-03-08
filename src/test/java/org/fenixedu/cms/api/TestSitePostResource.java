package org.fenixedu.cms.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.cms.api.bean.PostBean;
import org.fenixedu.cms.api.json.PostAdapter;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.CmsTestUtils;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.fenixframework.FenixFramework;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(FenixFrameworkRunner.class)
public class TestSitePostResource extends TestCmsApi {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestSitePostResource.class);

    @Test
    public void getSitePostsEmpty() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("getSitePostsEmpty");

        Site site = CmsTestUtils.createSite(user, "getSitePostsEmpty");

        // execute
        String response = getSitePostsTarget(site).request().get(String.class);
        LOGGER.debug("getSitePostsEmpty: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        //test
        assertTrue("New sites shouldn't have any posts yet", response.equals(EMPTY_RESPONSE));
    }

    @Test
    public void getSiteSeveralPosts() {
        // prepare
        Set<JsonElement> expectedJsonPosts = new HashSet<JsonElement>();

        User user = CmsTestUtils.createAuthenticatedUser("getSiteSeveralPosts");

        Site site = CmsTestUtils.createSite(user, "getSiteSeveralPosts");

        Post post1 = CmsTestUtils.createPost(site, "getSiteSeveralPosts1");
        JsonElement post1json = removeNullKeys(new PostAdapter().view(post1, ctx));
        expectedJsonPosts.add(post1json);

        Post post2 = CmsTestUtils.createPost(site, "getSiteSeveralPosts2");
        JsonElement post2json = removeNullKeys(new PostAdapter().view(post2, ctx));
        expectedJsonPosts.add(post2json);

        // execute
        String response = getSitePostsTarget(site).request().get(String.class);
        LOGGER.debug("getSiteSeveralPosts: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        //test
        assertTrue("post list from site shouldn't be empty", !response.isEmpty() && !response.equals(EMPTY_RESPONSE));

        JsonArray jsonResponseArray = new JsonParser().parse(response).getAsJsonArray();
        assertTrue("response should contain two posts", jsonResponseArray.size() == 2);

        assertTrue("response should include post1 and post2", expectedJsonPosts.contains(jsonResponseArray.get(0))
                && expectedJsonPosts.contains(jsonResponseArray.get(1)));
    }

    @Test
    public void getSitePostsBySingleCategory() {
        // prepare
        Set<JsonElement> expectedJsonPosts = new HashSet<JsonElement>();

        User user = CmsTestUtils.createAuthenticatedUser("getSitePostsBySingleCategory");

        Site site = CmsTestUtils.createSite(user, "getSitePostsBySingleCategory");

        Post post1 = CmsTestUtils.createPost(site, "getSitePostsBySingleCategory1");
        Post post2 = CmsTestUtils.createPost(site, "getSitePostsBySingleCategory2");
        Post post3 = CmsTestUtils.createPost(site, "getSitePostsBySingleCategory3");

        Category category = CmsTestUtils.createCategory(site, "getSitePostsBySingleCategory");

        addCategoryToPost(expectedJsonPosts, post2, category);
        addCategoryToPost(expectedJsonPosts, post3, category);

        // execute
        String response = getSitePostsTargetWithCategory(site, category).request().get(String.class);
        LOGGER.debug("getSitePostsBySingleCategory: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        //test
        assertTrue("post list from site shouldn't be empty", !response.isEmpty() && !response.equals(EMPTY_RESPONSE));

        JsonArray jsonResponseArray = new JsonParser().parse(response).getAsJsonArray();
        assertTrue("response should contain two posts", jsonResponseArray.size() == 2);

        assertTrue("response should include posts with category passed in query param",
                expectedJsonPosts.contains(jsonResponseArray.get(0)) && expectedJsonPosts.contains(jsonResponseArray.get(1)));
    }

    @Test
    public void getSitePostsByMultipleCategories() {
        // prepare
        Set<JsonElement> expectedJsonPosts = new HashSet<JsonElement>();

        User user = CmsTestUtils.createAuthenticatedUser("getSitePostsByMultipleCategories");

        Site site = CmsTestUtils.createSite(user, "getSitePostsByMultipleCategories");

        Post post1 = CmsTestUtils.createPost(site, "getSitePostsByMultipleCategories1");
        Post post2 = CmsTestUtils.createPost(site, "getSitePostsByMultipleCategories2");
        Post post3 = CmsTestUtils.createPost(site, "getSitePostsByMultipleCategories3");

        Category category1 = CmsTestUtils.createCategory(site, "getSitePostsByMultipleCategories1");
        Category category2 = CmsTestUtils.createCategory(site, "getSitePostsByMultipleCategories2");

        addCategoryToPost(expectedJsonPosts, post1, category1);
        addCategoryToPost(expectedJsonPosts, post2, category2);

        // execute
        WebTarget req = getSitePostsTargetWithCategories(site, category1, category2);
        LOGGER.debug(req.getUri().toString());

        String response = req.request().get(String.class);
        LOGGER.debug("getSitePostsByMultipleCategories: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        //test
        assertTrue("post list from site shouldn't be empty", !response.isEmpty() && !response.equals(EMPTY_RESPONSE));

        JsonArray jsonResponseArray = new JsonParser().parse(response).getAsJsonArray();
        assertTrue("response should contain two posts", jsonResponseArray.size() == 2);

        assertTrue("response should include posts with category passed in query param",
                expectedJsonPosts.contains(jsonResponseArray.get(0)) && expectedJsonPosts.contains(jsonResponseArray.get(1)));
    }

    public void addCategoryToPost(Set<JsonElement> expectedJsonPosts, Post post, Category category) {
        post.addCategories(category);
        JsonElement postJson = removeNullKeys(new PostAdapter().view(post, ctx));
        expectedJsonPosts.add(postJson);
    }

    @Test
    public void createMinPost() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("createMinPost");

        Site site = CmsTestUtils.createSite(user, "createMinPost");

        PostBean postBean = new PostBean();

        DateTime creationDate = new DateTime();

        // execute
        String response =
                getSitePostsTarget(site).request().post(Entity.entity(postBean.toJson(), MediaType.APPLICATION_JSON),
                        String.class);
        LOGGER.debug("createMinPost: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response post should have an id field", jsonResponse.has("id"));
        Post post = FenixFramework.getDomainObject(jsonResponse.get("id").getAsString());
        assertTrue("create endpoint should return id of an existing post", post != null);

        assertTrue("response post should have a site field", jsonResponse.has("site"));
        assertEquals("site response should be equal to created site", site.getExternalId(), jsonResponse.get("site")
                .getAsString());

        assertTrue("response site should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", creationDate.toString().substring(0, 16),
                jsonResponse // 16 to compare only date and time (hours and minutes) YYYY-MM-DD hh:mm
                        .get("creationDate").getAsString().substring(0, 16));

        assertTrue("response site should have an modificationDate field", jsonResponse.has("modificationDate"));
        assertEquals("modificationDate response should be equal to creationDate", jsonResponse.get("creationDate").getAsString(),
                jsonResponse.get("modificationDate").getAsString());

        assertTrue("response post should have a published field", jsonResponse.has("published"));
        assertEquals("published response should be equal to expected published", false, jsonResponse.get("published")
                .getAsBoolean());

        assertTrue("response site should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response post should have an body field", jsonResponse.has("body"));
        assertEquals("body response should be equal to expected empty body", new LocalizedString(),
                LocalizedString.fromJson(jsonResponse.get("body").getAsJsonObject()));

        assertFalse("response post should not contain slug", jsonResponse.has("slug"));
        assertFalse("response post should not contain name", jsonResponse.has("name"));
        assertFalse("response post should not contain publicationBegin", jsonResponse.has("publicationBegin"));
        assertFalse("response post should not contain publicationEnd", jsonResponse.has("publicationEnd"));
    }

    @Test
    public void createFullPost() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("createFullPost");

        Site site = CmsTestUtils.createSite(user, "createFullPost");

        LocalizedString name = new LocalizedString(Locale.UK, "createFullPost-name-uk").with(Locale.US, "createFullPost-name-us");
        LocalizedString body = new LocalizedString(Locale.UK, "createFullPost-body-uk").with(Locale.US, "createFullPost-body-us");
        PostBean postBean = new PostBean();
        postBean.setName(name);
        postBean.setBody(body);
        postBean.setSlug(StringNormalizer.slugify("createFullPost-slug"));

        DateTime creationDate = new DateTime();

        // execute
        String response =
                getSitePostsTarget(site).request().post(Entity.entity(postBean.toJson(), MediaType.APPLICATION_JSON),
                        String.class);
        LOGGER.debug("createMinPost: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response post should have an id field", jsonResponse.has("id"));
        Post post = FenixFramework.getDomainObject(jsonResponse.get("id").getAsString());
        assertTrue("create endpoint should return id of an existing post", post != null);

        assertTrue("response post should have a site field", jsonResponse.has("site"));
        assertEquals("site response should be equal to created site", site.getExternalId(), jsonResponse.get("site")
                .getAsString());

        assertTrue("response site should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", creationDate.toString().substring(0, 16),
                jsonResponse.get("creationDate").getAsString().substring(0, 16)); // 16 to compare only date and time (hours and minutes) YYYY-MM-DD hh:mm

        assertTrue("response site should have an modificationDate field", jsonResponse.has("modificationDate"));
        assertEquals("modificationDate response should be equal to creationDate", creationDate.toString().substring(0, 16),
                jsonResponse.get("modificationDate").getAsString().substring(0, 16));  // 16 to compare only date and time (hours and minutes) YYYY-MM-DD hh:mm

        assertTrue("response post should have a published field", jsonResponse.has("published"));
        assertEquals("published response should be equal to expected published", false, jsonResponse.get("published")
                .getAsBoolean());

        assertTrue("response site should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response post should have an body field", jsonResponse.has("body"));
        assertEquals("body response should be equal to expected body", body,
                LocalizedString.fromJson(jsonResponse.get("body").getAsJsonObject()));

        assertTrue("response post should have a name field", jsonResponse.has("name"));
        assertEquals("name response should be equal to expected name", name,
                LocalizedString.fromJson(jsonResponse.get("name").getAsJsonObject()));

        assertTrue("response post should have a slug field", jsonResponse.has("slug"));
        assertEquals("name response should be equal to expected name", postBean.getSlug(), jsonResponse.get("slug").getAsString());

        assertFalse("response post should not contain publicationBegin", jsonResponse.has("publicationBegin"));
        assertFalse("response post should not contain publicationEnd", jsonResponse.has("publicationEnd"));
    }
}
