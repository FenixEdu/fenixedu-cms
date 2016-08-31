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
import org.fenixedu.cms.api.bean.MenuItemBean;
import org.fenixedu.cms.api.json.MenuItemAdapter;
import org.fenixedu.cms.domain.CmsTestUtils;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.MenuItem;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RunWith(FenixFrameworkRunner.class)
public class TestMenuMenuItemResource extends TestCmsApi {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestMenuMenuItemResource.class);

    @Test
    public void getMenuMenuItemsEmpty() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("getMenuMenuItemsEmpty");

        Site site = CmsTestUtils.createSite(user, "getMenuMenuItemsEmpty");

        Menu menu = CmsTestUtils.createMenu(site, "getMenuMenuItemsEmpty");

        // execute
        String response = getMenuMenuItemsTarget(menu).request().get(String.class);
        LOGGER.debug("getMenuMenuItemsEmpty: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        //test
        assertTrue("New menus shouldn't have any menuItem yet", response.equals(EMPTY_RESPONSE));
    }

    @Test
    public void getMenuSeveralMenuItems() {
        // prepare
        Set<JsonElement> expectedJsonMenuItems = new HashSet<JsonElement>();

        User user = CmsTestUtils.createAuthenticatedUser("getMenuSeveralMenuItems");

        Site site = CmsTestUtils.createSite(user, "getMenuSeveralMenuItems");

        Menu menu = CmsTestUtils.createMenu(site, "getMenuSeveralMenuItems");

        MenuItem menuItem1 = CmsTestUtils.createMenuItem(menu, "getMenuSeveralMenuItems1");
        JsonElement menuItem1json = removeNullKeys(new MenuItemAdapter().view(menuItem1, ctx));
        expectedJsonMenuItems.add(menuItem1json);

        MenuItem menuItem2 = CmsTestUtils.createMenuItem(menu, "getMenuSeveralMenuItems2");
        JsonElement menuItem2json = removeNullKeys(new MenuItemAdapter().view(menuItem2, ctx));
        expectedJsonMenuItems.add(menuItem2json);

        // execute
        String response = getMenuMenuItemsTarget(menu).request().get(String.class);
        LOGGER.debug("getMenuSeveralMenuItems: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        //test
        assertTrue("menuItem list from site shouldn't be empty", !response.isEmpty() && !response.equals(EMPTY_RESPONSE));

        JsonArray jsonResponseArray = new JsonParser().parse(response).getAsJsonArray();
        assertTrue("response should contain two menuItems", jsonResponseArray.size() == 2);

        assertTrue("response should include menuItem1 and menuItem2", expectedJsonMenuItems.contains(jsonResponseArray.get(0))
                && expectedJsonMenuItems.contains(jsonResponseArray.get(1)));
    }

    @Test
    public void createMinMenuItem() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("createMinMenuItem");

        Site site = CmsTestUtils.createSite(user, "createMinMenuItem");

        Menu menu = CmsTestUtils.createMenu(site, "createMinMenuItem");

        MenuItemBean menuItemBean = new MenuItemBean();

        DateTime creationDate = new DateTime();

        // execute
        String response =
                getMenuMenuItemsTarget(menu).request().post(Entity.entity(menuItemBean.toJson(), MediaType.APPLICATION_JSON),
                        String.class);
        LOGGER.debug("createMinMenuItem: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response menuItem should have an id field", jsonResponse.has("id"));
        MenuItem menuItem = FenixFramework.getDomainObject(jsonResponse.get("id").getAsString());
        assertTrue("create endpoint should return id of an existing category", menu != null);

        assertTrue("response menuItem should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", creationDate.toString().substring(0, 16),
                jsonResponse // 16 to compare only date and time (hours and minutes) YYYY-MM-DD hh:mm
                        .get("creationDate").getAsString().substring(0, 16));

        assertTrue("response menuItem should have a folder field", jsonResponse.has("folder"));
        assertEquals("folder response should be false", false, jsonResponse.get("folder").getAsBoolean());

        assertTrue("response menuItem should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response menuItem should have a menu field", jsonResponse.has("menu"));
        assertEquals("menu response should be equal to menu site", menu.getExternalId(), jsonResponse.get("menu").getAsString());

        assertTrue("response menuItem should have a position field", jsonResponse.has("position"));
        assertEquals("position response should be equal default position", 0, jsonResponse.get("position").getAsInt());

        assertTrue("response menu should have a menuItems field", jsonResponse.has("menuItems"));
        JsonArray menuItems = jsonResponse.get("menuItems").getAsJsonArray();
        assertEquals("response menuItems should be empty", 0, menuItems.size());

        assertFalse("response menuItem should not contain url", jsonResponse.has("url"));
        assertFalse("response menuItem should not contain name", jsonResponse.has("name"));
    }

    @Test
    public void createFullMenuItem() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("createFullMenuItem");

        Site site = CmsTestUtils.createSite(user, "createFullMenuItem");

        Menu menu = CmsTestUtils.createMenu(site, "createFullMenuItem");

        MenuItemBean menuItemBean = new MenuItemBean();
        LocalizedString name =
                new LocalizedString(Locale.UK, "createFullMenuItem-name-uk").with(Locale.US, "createFullMenuItem-name-us");
        menuItemBean.setName(name);
        menuItemBean.setUrl("createFullMenuItem-url");
        menuItemBean.setFolder(true);
        menuItemBean.setPosition(99);

        DateTime creationDate = new DateTime();

        // execute
        String response =
                getMenuMenuItemsTarget(menu).request().post(Entity.entity(menuItemBean.toJson(), MediaType.APPLICATION_JSON),
                        String.class);
        LOGGER.debug("createFullMenuItem: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response menuItem should have an id field", jsonResponse.has("id"));
        MenuItem menuItem = FenixFramework.getDomainObject(jsonResponse.get("id").getAsString());
        assertTrue("create endpoint should return id of an existing category", menu != null);

        assertTrue("response menuItem should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", creationDate.toString().substring(0, 16),
                jsonResponse // 16 to compare only date and time (hours and minutes) YYYY-MM-DD hh:mm
                        .get("creationDate").getAsString().substring(0, 16));

        assertTrue("response menuItem should have a folder field", jsonResponse.has("folder"));
        assertEquals("folder response should be equal to menuItemBean", menuItemBean.getFolder(), jsonResponse.get("folder")
                .getAsBoolean());

        assertTrue("response menuItem should have a url field", jsonResponse.has("url"));
        assertEquals("url response should be equal to menuItemBean", menuItemBean.getUrl(), jsonResponse.get("url").getAsString());

        assertTrue("response menuItem should have a name field", jsonResponse.has("name"));
        assertEquals("name response should be equal to menuItemBean", menuItemBean.getName(),
                LocalizedString.fromJson(jsonResponse.get("name")));

        assertTrue("response menuItem should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response menuItem should have a menu field", jsonResponse.has("menu"));
        assertEquals("menu response should be equal to menu site", menu.getExternalId(), jsonResponse.get("menu").getAsString());

        assertTrue("response menuItem should have a position field", jsonResponse.has("position"));
        assertEquals("position response should be equal to menuItemBean", menuItemBean.getPosition(), new Integer(jsonResponse
                .get("position").getAsInt()));

        assertTrue("response menu should have a menuItems field", jsonResponse.has("menuItems"));
        JsonArray menuItems = jsonResponse.get("menuItems").getAsJsonArray();
        assertEquals("response menuItems should be empty", 0, menuItems.size());
    }
}
