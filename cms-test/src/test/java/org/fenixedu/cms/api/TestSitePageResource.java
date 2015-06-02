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
import org.fenixedu.cms.api.bean.PageBean;
import org.fenixedu.cms.api.domain.FenixFrameworkRunner;
import org.fenixedu.cms.api.json.PageAdapter;
import org.fenixedu.cms.domain.Page;
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
public class TestSitePageResource extends TestCmsApi {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestSitePageResource.class);

    @Test
    public void getSitePagesEmpty() {
        // prepare
        User user = createAuthenticatedUser("getSitePagesEmpty");

        Site site = createSite(user, "getSitePagesEmpty");

        // execute
        String response = getSitePagesTarget(site).request().get(String.class);
        LOGGER.debug("getSitePagesEmpty: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        //test
        assertTrue("New sites shouldn't have any pages yet", response.equals(EMPTY_RESPONSE));
    }

    @Test
    public void getSiteSeveralPages() {
        // prepare
        Set<JsonElement> expectedJsonPages = new HashSet<JsonElement>();

        User user = createAuthenticatedUser("getSiteSeveralPages");

        Site site = createSite(user, "getSiteSeveralPages");

        Page page1 = createPage(site, "getSiteSeveralPages1");
        JsonElement page1json = removeNullKeys(new PageAdapter().view(page1, ctx));
        expectedJsonPages.add(page1json);

        Page page2 = createPage(site, "getSiteSeveralPages2");
        JsonElement page2json = removeNullKeys(new PageAdapter().view(page2, ctx));
        expectedJsonPages.add(page2json);

        // execute
        String response = getSitePagesTarget(site).request().get(String.class);
        LOGGER.debug("getSiteSeveralPages: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        //test
        assertTrue("page list from site shouldn't be empty", !response.isEmpty() && !response.equals(EMPTY_RESPONSE));

        JsonArray jsonResponseArray = new JsonParser().parse(response).getAsJsonArray();
        assertTrue("response should contain two pages", jsonResponseArray.size() == 2);

        assertTrue("response should include page1 and page2", expectedJsonPages.contains(jsonResponseArray.get(0))
                && expectedJsonPages.contains(jsonResponseArray.get(1)));
    }

    @Test
    public void createMinPage() {
        // prepare
        User user = createAuthenticatedUser("createMinPage");

        Site site = createSite(user, "createMinPage");

        PageBean pageBean = new PageBean();

        DateTime creationDate = new DateTime();

        // execute
        String response =
                getSitePagesTarget(site).request().post(Entity.entity(pageBean.toJson(), MediaType.APPLICATION_JSON),
                        String.class);
        LOGGER.debug("createMinPage: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response page should have an id field", jsonResponse.has("id"));
        Page page = FenixFramework.getDomainObject(jsonResponse.get("id").getAsString());
        assertTrue("create endpoint should return id of an existing page", page != null);

        assertTrue("response page should have a site field", jsonResponse.has("site"));
        assertEquals("site response should be equal to created site", site.getExternalId(), jsonResponse.get("site")
                .getAsString());

        assertTrue("response page should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", creationDate.toString().substring(0, 16),
                jsonResponse // 16 to compare only date and time (hours and minutes) YYYY-MM-DD hh:mm
                .get("creationDate").getAsString().substring(0, 16));

        assertTrue("response page should have an modificationDate field", jsonResponse.has("modificationDate"));
        assertEquals("modificationDate response should be equal to creationDate", jsonResponse.get("creationDate").getAsString(),
                jsonResponse.get("modificationDate").getAsString());

        assertTrue("response page should have a published field", jsonResponse.has("published"));
        assertEquals("published response should be equal to expected published", true, jsonResponse.get("published")
                .getAsBoolean());

        assertTrue("response page should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertFalse("response page should not contain slug", jsonResponse.has("slug"));
        assertFalse("response page should not contain name", jsonResponse.has("name"));
    }

    @Test
    public void createFullPage() {
        // prepare
        User user = createAuthenticatedUser("createFullPage");

        Site site = createSite(user, "createFullPage");

        LocalizedString name = new LocalizedString(Locale.UK, "createFullPage-name-uk").with(Locale.US, "createFullPage-name-us");
        PageBean pageBean = new PageBean();
        pageBean.setName(name);
        pageBean.setSlug(StringNormalizer.slugify("createFullPage-slug"));
        pageBean.setPublished(false);

        DateTime creationDate = new DateTime();

        // execute
        String response =
                getSitePagesTarget(site).request().post(Entity.entity(pageBean.toJson(), MediaType.APPLICATION_JSON),
                        String.class);
        LOGGER.debug("createFullPage: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response page should have an id field", jsonResponse.has("id"));
        Page page = FenixFramework.getDomainObject(jsonResponse.get("id").getAsString());
        assertTrue("create endpoint should return id of an existing page", page != null);

        assertTrue("response page should have a site field", jsonResponse.has("site"));
        assertEquals("site response should be equal to created site", site.getExternalId(), jsonResponse.get("site")
                .getAsString());

        assertTrue("response page should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", creationDate.toString().substring(0, 16),
                jsonResponse.get("creationDate").getAsString().substring(0, 16)); // 16 to compare only date and time (hours and minutes) YYYY-MM-DD hh:mm

        assertTrue("response page should have an modificationDate field", jsonResponse.has("modificationDate"));
        assertEquals("modificationDate response should be equal to creationDate", creationDate.toString().substring(0, 16),
                jsonResponse.get("modificationDate").getAsString().substring(0, 16));  // 16 to compare only date and time (hours and minutes) YYYY-MM-DD hh:mm

        assertTrue("response page should have a published field", jsonResponse.has("published"));
        assertEquals("published response should be equal to expected published", false, jsonResponse.get("published")
                .getAsBoolean());

        assertTrue("response page should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response page should have a name field", jsonResponse.has("name"));
        assertEquals("name response should be equal to expected name", name,
                LocalizedString.fromJson(jsonResponse.get("name").getAsJsonObject()));

        assertTrue("response page should have a slug field", jsonResponse.has("slug"));
        assertEquals("name response should be equal to expected name", pageBean.getSlug(), jsonResponse.get("slug").getAsString());
    }
}
