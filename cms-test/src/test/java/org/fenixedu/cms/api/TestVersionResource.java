package org.fenixedu.cms.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.cms.api.domain.FenixFrameworkRunner;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.PostContentRevision;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RunWith(FenixFrameworkRunner.class)
public class TestVersionResource extends TestCmsApi {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestVersionResource.class);

    @Test
    public void getOnlyVersion() {
        // prepare
        User user = createAuthenticatedUser("getOnlyVersion");

        Site site = createSite(user, "getOnlyVersion");

        Post post = createPost(site, "getOnlyVersion");

        PostContentRevision version = post.getLatestRevision();

        // execute
        String response = getVersionTarget(version).request().get(String.class);
        LOGGER.debug("getOnlyVersion: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response version should have an id field", jsonResponse.has("id"));
        assertEquals("response version should have same id as the created", version.getExternalId(), jsonResponse.get("id")
                .getAsString());

        assertTrue("response version should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response version should have an body field", jsonResponse.has("body"));
        assertEquals("body response should be equal to expected body", version.getBody(),
                LocalizedString.fromJson(jsonResponse.get("body").getAsJsonObject()));

        assertTrue("response version should have a post field", jsonResponse.has("post"));
        assertEquals("post response should be equal to created post", post.getExternalId(), jsonResponse.get("post")
                .getAsString());

        assertTrue("response version should have an revisionDate field", jsonResponse.has("revisionDate"));
        assertEquals("revisionDate response should be equal to expected revisionDate", version.getRevisionDate().toString(),
                jsonResponse.get("revisionDate").getAsString());

        assertFalse("response version should not contain previous", jsonResponse.has("previous"));
        assertFalse("response version should not contain next", jsonResponse.has("next"));
    }

    @Test
    public void getMiddleVersion() {
        // prepare
        User user = createAuthenticatedUser("getMiddleVersion");

        Site site = createSite(user, "getMiddleVersion");

        Post post = createPost(site, "getMiddleVersion v1");
        PostContentRevision version1 = post.getLatestRevision();
        PostContentRevision version2 = createVersion(post, "getMiddleVersion v2");
        PostContentRevision version3 = createVersion(post, "getMiddleVersion v3");

        // execute
        String response = getVersionTarget(version2).request().get(String.class);
        LOGGER.debug("getOnlyVersion: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response version should have an id field", jsonResponse.has("id"));
        assertEquals("response version should have same id as the created", version2.getExternalId(), jsonResponse.get("id")
                .getAsString());

        assertTrue("response version should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response version should have an body field", jsonResponse.has("body"));
        assertEquals("body response should be equal to expected body", version2.getBody(),
                LocalizedString.fromJson(jsonResponse.get("body").getAsJsonObject()));

        assertTrue("response version should have a post field", jsonResponse.has("post"));
        assertEquals("post response should be equal to created post", post.getExternalId(), jsonResponse.get("post")
                .getAsString());

        assertTrue("response version should have an revisionDate field", jsonResponse.has("revisionDate"));
        assertEquals("revisionDate response should be equal to expected revisionDate", version2.getRevisionDate().toString(),
                jsonResponse.get("revisionDate").getAsString());

        assertTrue("response version should have an previous field", jsonResponse.has("previous"));
        assertEquals("previous response should be equal to expected previous", version1.getExternalId(),
                jsonResponse.get("previous").getAsString());

        assertTrue("response version should have an next field", jsonResponse.has("next"));
        assertEquals("next response should be equal to expected next", version3.getExternalId(), jsonResponse.get("next")
                .getAsString());
    }

}
