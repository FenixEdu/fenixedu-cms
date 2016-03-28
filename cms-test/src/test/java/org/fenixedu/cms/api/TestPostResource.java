package org.fenixedu.cms.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.cms.api.bean.PostBean;
import org.fenixedu.cms.api.json.PostAdapter;
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

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.Locale;

import static org.junit.Assert.*;

@RunWith(FenixFrameworkRunner.class)
public class TestPostResource extends TestCmsApi {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestPostResource.class);

    @Test
    public void getLastSingleVersion() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("getLastSingleVersion");
        Site site = CmsTestUtils.createSite(user, "getLastSingleVersion");

        Post post = new Post(site);
        LocalizedString postBody =
                new LocalizedString(Locale.UK, "post body uk getLastSingleVersion").with(Locale.US,
                        "post body us getLastSingleVersion");
        post.setBody(postBody);

        DateTime creationDate = new DateTime();

        // execute
        String response = getPostTarget(post).request().get(String.class);
        LOGGER.debug("getLastSingleVersion: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response post should have an id field", jsonResponse.has("id"));
        assertEquals("response post should have same id as the created", post.getExternalId(), jsonResponse.get("id")
                .getAsString());

        assertTrue("response post should have a site field", jsonResponse.has("site"));
        assertEquals("site response should be equal to created site", site.getExternalId(), jsonResponse.get("site")
                .getAsString());

        assertTrue("response site should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", creationDate.toString().substring(0, 16),
                jsonResponse // 16 to compare only date and time (hours and minutes) YYYY-MM-DD hh:mm
                        .get("creationDate").getAsString().substring(0, 16));

        assertTrue("response site should have an modificationDate field", jsonResponse.has("modificationDate"));
        assertEquals("modificationDate response should be equal to expected", post.getModificationDate().toString(), jsonResponse
                .get("modificationDate").getAsString());

        assertTrue("response post should have a published field", jsonResponse.has("published"));
        assertEquals("published response should be equal to expected published", false, jsonResponse.get("published")
                .getAsBoolean());

        assertTrue("response site should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response post should have an body field", jsonResponse.has("body"));
        assertEquals("body response should be equal to expected body", postBody,
                LocalizedString.fromJson(jsonResponse.get("body").getAsJsonObject()));

        assertFalse("response post should not contain slug", jsonResponse.has("slug"));
        assertFalse("response post should not contain name", jsonResponse.has("name"));
        assertFalse("response post should not contain publicationBegin", jsonResponse.has("publicationBegin"));
        assertFalse("response post should not contain publicationEnd", jsonResponse.has("publicationEnd"));
    }

    @Test
    public void getLastSeveralVersions() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("getLastSeveralVersions");
        Site site = CmsTestUtils.createSite(user, "getLastSeveralVersions");

        Post post = new Post(site);

        LocalizedString postBody1 =
                new LocalizedString(Locale.UK, "post body uk getLastSeveralVersions1").with(Locale.US,
                        "post body us getLastSeveralVersions1");
        post.setBody(postBody1);

        LocalizedString postBody2 =
                new LocalizedString(Locale.UK, "post body uk getLastSeveralVersions2").with(Locale.US,
                        "post body us getLastSeveralVersions2");
        post.setBody(postBody2);

        DateTime creationDate = new DateTime();

        // execute
        String response = getPostTarget(post).request().get(String.class);
        LOGGER.debug("getLastSeveralVersions: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response post should have an id field", jsonResponse.has("id"));
        assertEquals("response post should have same id as the created", post.getExternalId(), jsonResponse.get("id")
                .getAsString());

        assertTrue("response post should have a site field", jsonResponse.has("site"));
        assertEquals("site response should be equal to created site", site.getExternalId(), jsonResponse.get("site")
                .getAsString());

        assertTrue("response site should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", creationDate.toString().substring(0, 16),
                jsonResponse // 16 to compare only date and time (hours and minutes) YYYY-MM-DD hh:mm
                        .get("creationDate").getAsString().substring(0, 16));

        assertTrue("response site should have an modificationDate field", jsonResponse.has("modificationDate"));
        assertEquals("modificationDate response should be equal to expected", post.getModificationDate().toString(), jsonResponse
                .get("modificationDate").getAsString());

        assertTrue("response post should have a published field", jsonResponse.has("published"));
        assertEquals("published response should be equal to expected published", false, jsonResponse.get("published")
                .getAsBoolean());

        assertTrue("response site should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response post should have an body field", jsonResponse.has("body"));
        assertEquals("body response should be equal to latest body", postBody2,
                LocalizedString.fromJson(jsonResponse.get("body").getAsJsonObject()));

        assertFalse("response post should not contain slug", jsonResponse.has("slug"));
        assertFalse("response post should not contain name", jsonResponse.has("name"));
        assertFalse("response post should not contain publicationBegin", jsonResponse.has("publicationBegin"));
        assertFalse("response post should not contain publicationEnd", jsonResponse.has("publicationEnd"));
    }

    @Test
    public void editPost() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("editPost");

        Site site = CmsTestUtils.createSite(user, "editPost");

        Post post = CmsTestUtils.createPost(site, "editPost");

        LocalizedString nameEdit =
                new LocalizedString(Locale.UK, "post name uk nameEdit").with(Locale.US, "post name us nameEdit");
        LocalizedString bodyEdit =
                new LocalizedString(Locale.UK, "post body uk bodyEdit").with(Locale.US, "post body us bodyEdit");
        PostBean postBean = new PostBean();
        postBean.setName(nameEdit);
        postBean.setBody(bodyEdit);
        postBean.setSlug("editPost slug");
        postBean.setPublished(false);
        postBean.setPublicationBegin("2014-10-05T23:14");
        postBean.setPublicationEnd("2015-11-06T22:15");

        // execute
        String response =
                getPostTarget(post).request().put(Entity.entity(postBean.toJson(), MediaType.APPLICATION_JSON), String.class);
        LOGGER.debug("editPost: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        assertTrue("should get post in response", !response.isEmpty() && !response.equals(EMPTY_RESPONSE));

        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("edited post should contain name field", jsonResponse.has("name"));
        assertEquals("edit post should have edited name", postBean.getName(), LocalizedString.fromJson(jsonResponse.get("name")));

        assertTrue("edited post should contain body field", jsonResponse.has("body"));
        assertEquals("edit post should have edited body", postBean.getBody(), LocalizedString.fromJson(jsonResponse.get("body")));

        assertTrue("edited post should contain name slug", jsonResponse.has("slug"));
        assertEquals("edit post should have edited slug", StringNormalizer.slugify(postBean.getSlug()), jsonResponse.get("slug")
                .getAsString());

        assertTrue("edited post should contain published field", jsonResponse.has("published"));
        assertEquals("edit post should have edited published", postBean.getPublished(), jsonResponse.get("published")
                .getAsBoolean());

        assertTrue("edited Site should contain publicationBegin field", jsonResponse.has("publicationBegin"));
        assertEquals("edit site should have edited publicationBegin", PostAdapter.parseDate(postBean.getPublicationBegin())
                .toString(), jsonResponse.get("publicationBegin").getAsString());

        assertTrue("edited Site should contain publicationEnd field", jsonResponse.has("publicationEnd"));
        assertEquals("edit site should have edited publicationEnd", PostAdapter.parseDate(postBean.getPublicationEnd())
                .toString(), jsonResponse.get("publicationEnd").getAsString());
    }

    @Test
    public void editPostIdempotence() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("editPostIdempotence");

        Site site = CmsTestUtils.createSite(user, "editPostIdempotence");

        Post post = CmsTestUtils.createPost(site, "editPostIdempotence");

        LocalizedString nameEdit =
                new LocalizedString(Locale.UK, "post name uk nameEdit").with(Locale.US, "post name us nameEdit");
        LocalizedString bodyEdit =
                new LocalizedString(Locale.UK, "post body uk bodyEdit").with(Locale.US, "post body us bodyEdit");
        PostBean postBean = new PostBean();
        postBean.setName(nameEdit);
        postBean.setBody(bodyEdit);
        postBean.setSlug("editPost slug");
        postBean.setPublished(false);
        postBean.setPublicationBegin("2014-10-05T23:14");
        postBean.setPublicationEnd("2015-11-06T22:15");

        // execute
        String preResponse =
                getPostTarget(post).request().put(Entity.entity(postBean.toJson(), MediaType.APPLICATION_JSON), String.class);
        LOGGER.debug("editPostIdempotence: preResponse = " + preResponse.replaceAll("(\\r|\\n|\\t)", " "));

        String response =
                getPostTarget(post).request().put(Entity.entity(postBean.toJson(), MediaType.APPLICATION_JSON), String.class);
        LOGGER.debug("editPostIdempotence: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        JsonObject jsonPreResponse = new JsonParser().parse(preResponse).getAsJsonObject();
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        String preModificationDate = jsonPreResponse.get("modificationDate").getAsString();
        String modificationDate = jsonResponse.get("modificationDate").getAsString();

        assertEquals("edit method should be idempotent and not change modification date on consecutive calls",
                preModificationDate, modificationDate);

    }

}
