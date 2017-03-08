package org.fenixedu.cms.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.cms.api.bean.MenuItemBean;
import org.fenixedu.cms.domain.CmsTestUtils;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.MenuItem;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;
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
public class TestMenuItemResource extends TestCmsApi {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestMenuItemResource.class);

    @Test
    public void getMenuItem() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("getMenuItem");
        Site site = CmsTestUtils.createSite(user, "getMenuItem");
        Menu menu = CmsTestUtils.createMenu(site, "getMenuItem");
        MenuItem menuItem = CmsTestUtils.createMenuItem(menu, "getMenuItem");

        // execute
        String response = getMenuItemTarget(menuItem).request().get(String.class);
        LOGGER.debug("getMenuItem: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response menuItem should have an id field", jsonResponse.has("id"));
        assertEquals("response menuItem should have same id as the created", menuItem.getExternalId(), jsonResponse.get("id")
                .getAsString());

        assertTrue("response menuItem should have a name field", jsonResponse.has("name"));
        assertEquals("name response should be equal to expected name", menuItem.getName(),
                LocalizedString.fromJson(jsonResponse.get("name").getAsJsonObject()));

        assertTrue("response menuItem should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", menuItem.getCreationDate().toString(),
                jsonResponse.get("creationDate").getAsString());

        assertTrue("response menuItem should have a position field", jsonResponse.has("position"));
        assertEquals("position response should be equal default position", 0, jsonResponse.get("position").getAsInt());

        assertTrue("response menuItem should have a folder field", jsonResponse.has("folder"));
        assertEquals("folder response should be false", false, jsonResponse.get("folder").getAsBoolean());

        assertTrue("response menuItem should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response menuItem should have a menu field", jsonResponse.has("menu"));
        assertEquals("menu response should be equal to menu site", menu.getExternalId(), jsonResponse.get("menu").getAsString());

        assertTrue("response menuItem should have an menuItems field", jsonResponse.has("menuItems"));
        assertEquals("menuItems (child) list should be empty", 0, jsonResponse.get("menuItems").getAsJsonArray().size());

        assertFalse("response menuItem should not contain url", jsonResponse.has("url"));
    }

    @Test
    public void editMenuItem() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("editMenuItem");
        Site site = CmsTestUtils.createSite(user, "editMenuItem");
        Menu menu = CmsTestUtils.createMenu(site, "editMenuItem");
        MenuItem menuItem = CmsTestUtils.createMenuItem(menu, "editMenuItem");

        LocalizedString nameEdit =
                new LocalizedString(Locale.UK, "menuItem name uk nameEdit").with(Locale.US, "menuItem name us nameEdit");
        MenuItemBean menuItemBean = new MenuItemBean();
        menuItemBean.setName(nameEdit);
        menuItemBean.setPosition(99);
        menuItemBean.setFolder(true);
        menuItemBean.setUrl("editMenuItem-url");

        // execute
        String response =
                getMenuItemTarget(menuItem).request().put(Entity.entity(menuItemBean.toJson(), MediaType.APPLICATION_JSON),
                        String.class);
        LOGGER.debug("editMenuItem: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        assertTrue("should get menuItem in response", !response.isEmpty() && !response.equals(EMPTY_RESPONSE));

        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("edit menuItem should have an id field", jsonResponse.has("id"));
        assertEquals("edit menuItem should have same id", menuItem.getExternalId(), jsonResponse.get("id").getAsString());

        assertTrue("response menuItem should have a name field", jsonResponse.has("name"));
        assertEquals("edit name response should have edited name name", menuItemBean.getName(),
                LocalizedString.fromJson(jsonResponse.get("name").getAsJsonObject()));

        assertTrue("response menuItem should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", menuItem.getCreationDate().toString(),
                jsonResponse.get("creationDate").getAsString());

        assertTrue("response menuItem should have a position field", jsonResponse.has("position"));
        assertEquals("position response should have edited position", menuItemBean.getPosition(),
                new Integer(jsonResponse.get("position").getAsInt()));

        assertTrue("response menuItem should have a folder field", jsonResponse.has("folder"));
        assertEquals("folder response should be false", menuItemBean.getFolder(), jsonResponse.get("folder").getAsBoolean());

        assertTrue("response menuItem should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response menuItem should have a menu field", jsonResponse.has("menu"));
        assertEquals("menu response should be equal to menu site", menu.getExternalId(), jsonResponse.get("menu").getAsString());

        assertTrue("response menuItem should have an menuItems field", jsonResponse.has("menuItems"));
        assertEquals("menuItems (child) list should be empty", 0, jsonResponse.get("menuItems").getAsJsonArray().size());

        assertTrue("response menuItem should have an url field", jsonResponse.has("url"));
        assertEquals("edit url should have edit url", menuItemBean.getUrl(), jsonResponse.get("url").getAsString());
    }

}
