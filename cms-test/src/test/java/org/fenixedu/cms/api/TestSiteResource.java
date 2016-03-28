package org.fenixedu.cms.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.api.bean.SiteBean;
import org.fenixedu.cms.api.json.SiteAdapter;
import org.fenixedu.cms.domain.CMSTheme;
import org.fenixedu.cms.domain.CmsTestUtils;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(FenixFrameworkRunner.class)
public class TestSiteResource extends TestCmsApi {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestSiteResource.class);

    @Test
    public void listUnloggedUserAllSites() {
        Authenticate.unmock();
        String response = getSitesTarget().request().get(String.class);
        LOGGER.debug("listUnloggedUserAllSites: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));
        assertTrue("Not logged request return any site", response.equals(EMPTY_RESPONSE));
    }

    @Test
    public void listNewUserAllSites() {
        // prepare
        CmsTestUtils.createAuthenticatedUser("listNewUserAllSites");

        // execute
        String response = getSitesTarget().request().get(String.class);
        LOGGER.debug("listNewUserAllSites: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        assertTrue("New users shouldn't be admin of any site yet", response.equals(EMPTY_RESPONSE));
    }

    @Test
    public void listUserSingleSites() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("listUserSingleSites");

        Site site = CmsTestUtils.createSite(user, "listUserSingleSites");

        // execute
        String response = getSitesTarget().request().get(String.class);
        LOGGER.debug("listUserSingleSites: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        assertTrue("user site admin should get site in response", !response.isEmpty() && !response.equals(EMPTY_RESPONSE));

        JsonArray jsonResponseArray = new JsonParser().parse(response).getAsJsonArray();
        assertTrue("response should contain only a single site", jsonResponseArray.size() == 1);

        JsonObject jsonResponse = jsonResponseArray.get(0).getAsJsonObject();

        JsonElement siteJson = removeNullKeys(new SiteAdapter().view(site, new JsonBuilder()));

        assertEquals("response should be equals to site json", siteJson, jsonResponse);
    }

    @Test
    public void listUserSeveralSites() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("listUserSeveralSites");

        Set<JsonElement> expectedJsonSites = new HashSet<JsonElement>();

        Site site1 = CmsTestUtils.createSite(user, "listUserSeveralSites1");
        JsonElement site1json = removeNullKeys(new SiteAdapter().view(site1, ctx));
        expectedJsonSites.add(site1json);

        Site site2 = CmsTestUtils.createSite(user, "listUserSeveralSites2");
        JsonElement site2json = removeNullKeys(new SiteAdapter().view(site2, ctx));
        expectedJsonSites.add(site2json);

        // execute
        String response = getSitesTarget().request().get(String.class);
        LOGGER.debug("listUserSeveralSites: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        assertTrue("user site admin should get site in response", !response.isEmpty() && !response.equals(EMPTY_RESPONSE));

        JsonArray jsonResponseArray = new JsonParser().parse(response).getAsJsonArray();
        assertTrue("response should contain two sites", jsonResponseArray.size() == 2);

        assertTrue("response should include site1 and site2", expectedJsonSites.contains(jsonResponseArray.get(0))
                && expectedJsonSites.contains(jsonResponseArray.get(1)));
    }

    @Test
    public void createErrorSiteWithoutName() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("createErrorSiteWithoutName");

        LocalizedString description =
                new LocalizedString(Locale.UK, "createErrorSiteWithoutName-description-uk").with(Locale.US,
                        "createErrorSiteWithoutName-description-us");
        SiteBean siteBean = new SiteBean(null, description);

        // execute
        Response response =
                getSitesTarget().request().post(Entity.entity(siteBean.toJson(), MediaType.APPLICATION_JSON), Response.class);
        LOGGER.debug("createErrorSiteWithoutName: response = " + response.getStatus() + " (" + response.getStatusInfo() + ")");
        assertEquals(412, response.getStatus());
    }

    @Test
    public void createErrorSiteWithoutDescription() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("createErrorSiteWithoutDescription");

        LocalizedString name =
                new LocalizedString(Locale.UK, "createErrorSiteWithoutDescription-name-uk").with(Locale.US,
                        "createErrorSiteWithoutDescription-name-us");
        SiteBean siteBean = new SiteBean(name, null);

        // execute
        Response response =
                getSitesTarget().request().post(Entity.entity(siteBean.toJson(), MediaType.APPLICATION_JSON), Response.class);
        LOGGER.debug("createErrorSiteWithoutDescription: response = " + response.getStatus() + " (" + response.getStatusInfo()
                + ")");
        assertEquals(412, response.getStatus());
    }

    @Test
    public void createMinSite() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("createMinSite");

        LocalizedString name = new LocalizedString(Locale.UK, "createMinSite-name-uk").with(Locale.US, "createMinSite-name-us");
        LocalizedString description =
                new LocalizedString(Locale.UK, "createMinSite-description-uk").with(Locale.US, "createMinSite-description-us");
        SiteBean siteBean = new SiteBean(name, description);

        DateTime creationDate = new DateTime();

        // execute
        String response =
                getSitesTarget().request().post(Entity.entity(siteBean.toJson(), MediaType.APPLICATION_JSON), String.class);
        LOGGER.debug("createMinSite: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response site should have an id field", jsonResponse.has("id"));
        Site site = FenixFramework.getDomainObject(jsonResponse.get("id").getAsString());
        assertTrue("create endpoint should return id of an existing site", site != null);

        assertTrue("response site should have a slug field", jsonResponse.has("slug"));
        assertEquals("slug response should be equal to expected slug", StringNormalizer.slugify(name.getContent()), jsonResponse
                .get("slug").getAsString());

        assertTrue("response site should have an name field", jsonResponse.has("name"));
        assertEquals("name response should be equal to expected name", name,
                LocalizedString.fromJson(jsonResponse.get("name").getAsJsonObject()));

        assertTrue("response site should have an description field", jsonResponse.has("description"));
        assertEquals("description response should be equal to expected description", description,
                LocalizedString.fromJson(jsonResponse.get("description").getAsJsonObject()));

        assertTrue("response site should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", creationDate.toString().substring(0, 16),
                jsonResponse // 16 to compare only date and time (hours and minutes) YYYY-MM-DD hh:mm
                .get("creationDate").getAsString().substring(0, 16));

        assertTrue("response site should have a published field", jsonResponse.has("published"));
        assertEquals("published response should be equal to expected published", false, jsonResponse.get("published")
                .getAsBoolean());

        assertTrue("response site should have an embedded field", jsonResponse.has("embedded"));
        assertEquals("embedded response should be equal to expected embedded", false, jsonResponse.get("embedded").getAsBoolean());

        assertTrue("response site should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertFalse("response site should not contain alternativeSite", jsonResponse.has("alternativeSite"));
        assertFalse("response site should not contain analyticsCode", jsonResponse.has("analyticsCode"));
        assertFalse("response site should not contain theme", jsonResponse.has("theme"));
    }

    @Test
    public void createFullSite() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("createFullSite");

        CMSTheme theme = new CMSTheme();
        theme.setType("createFullSite-theme-type");

        LocalizedString name = new LocalizedString(Locale.UK, "createFullSite-name-uk").with(Locale.US, "createFullSite-name-us");
        LocalizedString description =
                new LocalizedString(Locale.UK, "createFullSite-description-uk").with(Locale.US, "createFullSite-description-us");
        SiteBean siteBean = new SiteBean(name, description);
        siteBean.setEmbedded(true);
        siteBean.setTheme(theme.getExternalId());

        DateTime creationDate = new DateTime();

        // execute
        String response =
                getSitesTarget().request().post(Entity.entity(siteBean.toJson(), MediaType.APPLICATION_JSON), String.class);
        LOGGER.debug("createFullSite: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response site should have an id field", jsonResponse.has("id"));
        Site site = FenixFramework.getDomainObject(jsonResponse.get("id").getAsString());
        assertTrue("create endpoint should return in of an existing site", site != null);

        assertTrue("response site should have a slug field", jsonResponse.has("slug"));
        assertEquals("slug response should be equal to expected slug", StringNormalizer.slugify(name.getContent()), jsonResponse
                .get("slug").getAsString());

        assertTrue("response site should have an name field", jsonResponse.has("name"));
        assertEquals("name response should be equal to expected name", name,
                LocalizedString.fromJson(jsonResponse.get("name").getAsJsonObject()));

        assertTrue("response site should have an description field", jsonResponse.has("description"));
        assertEquals("description response should be equal to expected description", description,
                LocalizedString.fromJson(jsonResponse.get("description").getAsJsonObject()));

        assertTrue("response site should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", creationDate.toString().substring(0, 16),
                jsonResponse // 16 to compare only date and time (hours and minutes) YYYY-MM-DD hh:mm
                .get("creationDate").getAsString().substring(0, 16));

        assertTrue("response site should have a published field", jsonResponse.has("published"));
        assertEquals("published response should be equal to expected published", false, jsonResponse.get("published")
                .getAsBoolean());

        assertTrue("response site should have an embedded field", jsonResponse.has("embedded"));
        assertEquals("embedded response should be equal to expected embedded", true, jsonResponse.get("embedded").getAsBoolean());

        assertTrue("response site should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response site should have an theme field", jsonResponse.has("theme"));
        assertEquals("theme response should be equal to expected theme", theme.getExternalId(), jsonResponse.get("theme")
                .getAsString());

        assertFalse("response site should not contain alternativeSite", jsonResponse.has("alternativeSite"));
        assertFalse("response site should not contain analyticsCode", jsonResponse.has("analyticsCode"));
    }

    @Test
    public void getSite() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("getSite");

        Site site = CmsTestUtils.createSite(user, "getSite");

        // execute
        String response = getSiteTarget(site).request().get(String.class);
        LOGGER.debug("getSite: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        assertTrue("should get site in response", !response.isEmpty() && !response.equals(EMPTY_RESPONSE));

        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        JsonElement siteJson = removeNullKeys(new SiteAdapter().view(site, new JsonBuilder()));

        assertEquals("response should be equals to site json", siteJson, jsonResponse);
    }

    @Test
    public void editSite() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("editSite");

        Site site = CmsTestUtils.createSite(user, "editSite");

        LocalizedString nameEdit =
                new LocalizedString(Locale.UK, "site name uk nameEdit").with(Locale.US, "site name us nameEdit");
        LocalizedString descriptionEdit =
                new LocalizedString(Locale.UK, "site description uk descriptionEdit").with(Locale.US,
                        "site description us descriptionEdit");
        SiteBean siteBean = new SiteBean(nameEdit, descriptionEdit);
        CMSTheme theme = new CMSTheme();
        theme.setType("createFullSite-theme-type");
        siteBean.setTheme(theme.getExternalId());
        theme.setType("createFullSite-theme-type");
        siteBean.setSlug("editSite slug");
        siteBean.setAnalyticsCode("editSite analyticsCode");
        siteBean.setAlternativeSite("editSite alternativeSite");
        siteBean.setPublished(true);

        // execute
        String response =
                getSiteTarget(site).request().put(Entity.entity(siteBean.toJson(), MediaType.APPLICATION_JSON), String.class);
        // test
        LOGGER.debug("editSite: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        assertTrue("should get site in response", !response.isEmpty() && !response.equals(EMPTY_RESPONSE));

        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("edited Site should contain name field", jsonResponse.has("name"));
        assertEquals("edit site should have edited name", siteBean.getName(), LocalizedString.fromJson(jsonResponse.get("name")));

        assertTrue("edited Site should contain description field", jsonResponse.has("description"));
        assertEquals("edit site should have edited description", siteBean.getDescription(),
                LocalizedString.fromJson(jsonResponse.get("description")));

        assertTrue("edited Site should contain theme field", jsonResponse.has("theme"));
        assertEquals("edit site should have edited theme", siteBean.getTheme(), jsonResponse.get("theme").getAsString());

        assertTrue("edited Site should contain name slug", jsonResponse.has("slug"));
        assertEquals("edit site should have edited slug", StringNormalizer.slugify(siteBean.getSlug()), jsonResponse.get("slug")
                .getAsString());

        assertTrue("edited Site should contain name analyticsCode", jsonResponse.has("analyticsCode"));
        assertEquals("edit site should have edited analyticsCode", siteBean.getAnalyticsCode(), jsonResponse.get("analyticsCode")
                .getAsString());

        assertTrue("edited Site should contain name alternativeSite", jsonResponse.has("alternativeSite"));
        assertEquals("edit site should have edited alternativeSite", siteBean.getAlternativeSite(),
                jsonResponse.get("alternativeSite").getAsString());

        assertTrue("edited Site should contain name published", jsonResponse.has("published"));
        assertEquals("edit site should have edited published", siteBean.getPublished(), jsonResponse.get("published")
                .getAsBoolean());
    }

}
