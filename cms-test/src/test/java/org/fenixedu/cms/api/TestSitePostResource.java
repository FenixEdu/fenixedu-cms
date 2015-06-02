package org.fenixedu.cms.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.cms.api.bean.PostBean;
import org.fenixedu.cms.api.domain.FenixFrameworkRunner;
import org.fenixedu.cms.api.json.PostAdapter;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RunWith(FenixFrameworkRunner.class)
public class TestSitePostResource extends TestCmsApi {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestSitePostResource.class);

    @Test
    public void getSitePostsEmpty() {
        // prepare
        User user = createAuthenticatedUser("getSitePostsEmpty");

        Site site = createSite(user, "getSitePostsEmpty");

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

        User user = createAuthenticatedUser("getSiteSeveralPosts");

        Site site = createSite(user, "getSiteSeveralPosts");

        Post post1 = createPost(site, "getSiteSeveralPosts1");
        JsonElement post1json = removeNullKeys(new PostAdapter().view(post1, ctx));
        expectedJsonPosts.add(post1json);

        Post post2 = createPost(site, "getSiteSeveralPosts2");
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
    public void createMinPost() {
        // prepare
        User user = createAuthenticatedUser("createMinPost");

        Site site = createSite(user, "createMinPost");

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
        assertEquals("published response should be equal to expected published", true, jsonResponse.get("published")
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
        User user = createAuthenticatedUser("createFullPost");

        Site site = createSite(user, "createFullPost");

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
        assertEquals("published response should be equal to expected published", true, jsonResponse.get("published")
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
