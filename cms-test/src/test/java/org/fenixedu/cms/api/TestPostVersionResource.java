package org.fenixedu.cms.api;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.cms.api.json.PostRevisionAdapter;
import org.fenixedu.cms.domain.CmsTestUtils;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.PostContentRevision;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@RunWith(FenixFrameworkRunner.class)
public class TestPostVersionResource extends TestCmsApi {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestPostVersionResource.class);

    @Test
    public void getPostSeveralVersions() {
        // prepare
        Set<JsonElement> expectedJsonVersions = new HashSet<JsonElement>();

        User user = CmsTestUtils.createAuthenticatedUser("getPostSeveralVersions");

        Site site = CmsTestUtils.createSite(user, "getPostSeveralVersions");

        Post post = CmsTestUtils.createPost(site, "getPostSeveralVersions");

        PostContentRevision version1 = post.getLatestRevision();

        LocalizedString postEditBody =
                new LocalizedString(Locale.UK, "post body uk edit " + "getPostSeveralVersions").with(Locale.US,
                        "post body us edit " + "getPostSeveralVersions");
        post.setBody(postEditBody);
        PostContentRevision version2 = post.getLatestRevision();

        JsonElement version1json = removeNullKeys(new PostRevisionAdapter().view(version1, ctx));
        expectedJsonVersions.add(version1json);
        JsonElement version2json = removeNullKeys(new PostRevisionAdapter().view(version2, ctx));
        expectedJsonVersions.add(version2json);

        // execute
        String response = getPostVersionsTarget(post).request().get(String.class);
        LOGGER.debug("getPostSeveralVersions: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        //test
        assertTrue("version list from postshouldn't be empty", !response.isEmpty() && !response.equals(EMPTY_RESPONSE));

        JsonArray jsonResponseArray = new JsonParser().parse(response).getAsJsonArray();
        assertTrue("response should contain two versions", jsonResponseArray.size() == 2);

        assertTrue("response should include version1 and version2", expectedJsonVersions.contains(jsonResponseArray.get(0))
                && expectedJsonVersions.contains(jsonResponseArray.get(1)));
    }

}
