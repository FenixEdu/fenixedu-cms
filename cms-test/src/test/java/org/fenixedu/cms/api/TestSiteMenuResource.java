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
import org.fenixedu.cms.api.bean.MenuBean;
import org.fenixedu.cms.api.domain.FenixFrameworkRunner;
import org.fenixedu.cms.api.json.MenuAdapter;
import org.fenixedu.cms.domain.Menu;
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
public class TestSiteMenuResource extends TestCmsApi {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestSiteMenuResource.class);

    @Test
    public void getSiteMenusEmpty() {
        // prepare
        User user = createAuthenticatedUser("getSiteMenusEmpty");

        Site site = createSite(user, "getSiteMenusEmpty");

        // execute
        String response = getSiteMenusTarget(site).request().get(String.class);
        LOGGER.debug("getSiteMenusEmpty: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        //test
        assertTrue("New sites shouldn't have any menu yet", response.equals(EMPTY_RESPONSE));
    }

    @Test
    public void getSiteSeveralMenus() {
        // prepare
        Set<JsonElement> expectedJsonMenus = new HashSet<JsonElement>();

        User user = createAuthenticatedUser("getSiteSeveralMenus");

        Site site = createSite(user, "getSiteSeveralMenus");

        Menu menu1 = createMenu(site, "getSiteSeveralMenus1");
        JsonElement menu1json = removeNullKeys(new MenuAdapter().view(menu1, ctx));
        expectedJsonMenus.add(menu1json);

        Menu menu2 = createMenu(site, "getSiteSeveralMenus2");
        JsonElement menu2json = removeNullKeys(new MenuAdapter().view(menu2, ctx));
        expectedJsonMenus.add(menu2json);

        // execute
        String response = getSiteMenusTarget(site).request().get(String.class);
        LOGGER.debug("getSiteSeveralMenus: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        //test
        assertTrue("menu list from site shouldn't be empty", !response.isEmpty() && !response.equals(EMPTY_RESPONSE));

        JsonArray jsonResponseArray = new JsonParser().parse(response).getAsJsonArray();
        assertTrue("response should contain two menus", jsonResponseArray.size() == 2);

        assertTrue("response should include menu1 and menu2", expectedJsonMenus.contains(jsonResponseArray.get(0))
                && expectedJsonMenus.contains(jsonResponseArray.get(1)));
    }

    @Test
    public void createMinMenu() {
        // prepare
        User user = createAuthenticatedUser("createMinMenu");

        Site site = createSite(user, "createMinMenu");

        MenuBean menuBean = new MenuBean();

        DateTime creationDate = new DateTime();

        // execute
        String response =
                getSiteMenusTarget(site).request().post(Entity.entity(menuBean.toJson(), MediaType.APPLICATION_JSON),
                        String.class);
        LOGGER.debug("createMinMenu: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response menu should have an id field", jsonResponse.has("id"));
        Menu menu = FenixFramework.getDomainObject(jsonResponse.get("id").getAsString());
        assertTrue("create endpoint should return id of an existing category", menu != null);

        assertTrue("response menu should have a topMenu field", jsonResponse.has("topMenu"));
        assertEquals("topMenu response should be false", false, jsonResponse.get("topMenu").getAsBoolean());

        assertTrue("response menu should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", creationDate.toString().substring(0, 16),
                jsonResponse // 16 to compare only date and time (hours and minutes) YYYY-MM-DD hh:mm
                .get("creationDate").getAsString().substring(0, 16));

        assertTrue("response category should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response menu should have a site field", jsonResponse.has("site"));
        assertEquals("site response should be equal to created site", site.getExternalId(), jsonResponse.get("site")
                .getAsString());

        assertTrue("response menu should have a menuItems field", jsonResponse.has("menuItems"));
        JsonArray menuItems = jsonResponse.get("menuItems").getAsJsonArray();
        assertEquals("response menuItems should be empty", 0, menuItems.size());

        assertFalse("response category should not contain slug", jsonResponse.has("slug"));
        assertFalse("response category should not contain name", jsonResponse.has("name"));
    }

    @Test
    public void createFullMenu() {
        // prepare
        User user = createAuthenticatedUser("createFullMenu");

        Site site = createSite(user, "createFullMenu");

        MenuBean menuBean = new MenuBean();
        LocalizedString name = new LocalizedString(Locale.UK, "createFullMenu-name-uk").with(Locale.US, "createFullMenu-name-us");
        menuBean.setName(name);
        menuBean.setSlug(StringNormalizer.slugify("createFullMenu-slug"));
        menuBean.setTopMenu(true);

        DateTime creationDate = new DateTime();

        // execute
        String response =
                getSiteMenusTarget(site).request().post(Entity.entity(menuBean.toJson(), MediaType.APPLICATION_JSON),
                        String.class);
        LOGGER.debug("createFullMenu: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response menu should have an id field", jsonResponse.has("id"));
        Menu menu = FenixFramework.getDomainObject(jsonResponse.get("id").getAsString());
        assertTrue("create endpoint should return id of an existing category", menu != null);

        assertTrue("response menu should have a topMenu field", jsonResponse.has("topMenu"));
        assertEquals("topMenu response should be true", true, jsonResponse.get("topMenu").getAsBoolean());

        assertTrue("response menu should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", creationDate.toString().substring(0, 16),
                jsonResponse // 16 to compare only date and time (hours and minutes) YYYY-MM-DD hh:mm
                .get("creationDate").getAsString().substring(0, 16));

        assertTrue("response category should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response menu should have a site field", jsonResponse.has("site"));
        assertEquals("site response should be equal to created site", site.getExternalId(), jsonResponse.get("site")
                .getAsString());

        assertTrue("response menu should have a menuItems field", jsonResponse.has("menuItems"));
        JsonArray menuItems = jsonResponse.get("menuItems").getAsJsonArray();
        assertEquals("response menuItems should be empty", 0, menuItems.size());

        assertTrue("response menu should have a name field", jsonResponse.has("name"));
        assertEquals("name response should be equal to expected name", name,
                LocalizedString.fromJson(jsonResponse.get("name").getAsJsonObject()));

        assertTrue("response menu should have a slug field", jsonResponse.has("slug"));
        assertEquals("slug response should be equal to expected slug", menuBean.getSlug(), jsonResponse.get("slug").getAsString());
    }
}
