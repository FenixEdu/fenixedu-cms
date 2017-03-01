package org.fenixedu.cms.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.cms.api.bean.MenuBean;
import org.fenixedu.cms.domain.CmsTestUtils;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.MenuItem;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.commons.i18n.LocalizedString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(FenixFrameworkRunner.class)
public class TestMenuResource extends TestCmsApi {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestMenuResource.class);

    @Test
    public void getMenu() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("getMenu");
        Site site = CmsTestUtils.createSite(user, "getMenu");
        Menu menu = CmsTestUtils.createMenu(site, "getMenu");
        MenuItem menuItem1 = new MenuItem(menu);
        menu.addToplevelItems(menuItem1);

        MenuItem menuItem2 = new MenuItem(menu);
        menu.addToplevelItems(menuItem2);

        // execute
        String response = getMenuTarget(menu).request().get(String.class);
        LOGGER.debug("getMenu: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response menu should have an id field", jsonResponse.has("id"));
        assertEquals("response menu should have same id as the created", menu.getExternalId(), jsonResponse.get("id")
                .getAsString());

        assertTrue("response menu should have a slug field", jsonResponse.has("slug"));
        assertEquals("name response should be equal to expected name", menu.getSlug(), jsonResponse.get("slug").getAsString());

        assertTrue("response menu should have a topMenu field", jsonResponse.has("topMenu"));
        assertEquals("topMenu response should be equal to expected published", false, jsonResponse.get("topMenu").getAsBoolean());

        assertTrue("response menu should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", menu.getCreationDate().toString(),
                jsonResponse.get("creationDate").getAsString());

        assertTrue("response menu should have a name field", jsonResponse.has("name"));
        assertEquals("name response should be equal to expected name", menu.getName(),
                LocalizedString.fromJson(jsonResponse.get("name").getAsJsonObject()));

        assertTrue("response menu should have a site field", jsonResponse.has("site"));
        assertEquals("site response should be equal to created site", site.getExternalId(), jsonResponse.get("site")
                .getAsString());

        assertTrue("response menu should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response menu should have an menuItems field", jsonResponse.has("menuItems"));
        assertEquals("menuItems list should contain 2 created menuItems", 2, jsonResponse.get("menuItems").getAsJsonArray()
                .size());
    }

    @Test
    public void editMenu() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("editMenu");
        Site site = CmsTestUtils.createSite(user, "editMenu");
        Menu menu = CmsTestUtils.createMenu(site, "editMenu");

        LocalizedString nameEdit =
                new LocalizedString(Locale.UK, "menu name uk nameEdit").with(Locale.US, "menu name us nameEdit");
        MenuBean menuBean = new MenuBean();
        menuBean.setName(nameEdit);
        menuBean.setSlug("editMenu-slug");

        // execute
        String response =
                getMenuTarget(menu).request().put(Entity.entity(menuBean.toJson(), MediaType.APPLICATION_JSON), String.class);
        LOGGER.debug("editMenu: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        assertTrue("should get menu in response", !response.isEmpty() && !response.equals(EMPTY_RESPONSE));

        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("edit menu should have an id field", jsonResponse.has("id"));
        assertEquals("edit menu should have same id", menu.getExternalId(), jsonResponse.get("id").getAsString());

        assertTrue("edited menu should contain name slug", jsonResponse.has("slug"));
        assertEquals("edit menu should have edited slug", StringNormalizer.slugify(menuBean.getSlug()), jsonResponse.get("slug")
                .getAsString());

        assertTrue("edited menu should contain topMenu field", jsonResponse.has("topMenu"));
        assertEquals("edit menu should have same topMenu", menu.getTopMenu(), jsonResponse.get("topMenu").getAsBoolean());

        assertTrue("response menu should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", menu.getCreationDate().toString(),
                jsonResponse.get("creationDate").getAsString());

        assertTrue("edited menu should contain name field", jsonResponse.has("name"));
        assertEquals("edit menu should have edited name", menuBean.getName(), LocalizedString.fromJson(jsonResponse.get("name")));

        assertTrue("response menu should have a site field", jsonResponse.has("site"));
        assertEquals("site response should be equal to created site", site.getExternalId(), jsonResponse.get("site")
                .getAsString());

        assertTrue("response menu should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response menu should have an menuItems field", jsonResponse.has("menuItems"));
        assertEquals("menuItems list should be empty", 0, jsonResponse.get("menuItems").getAsJsonArray().size());
    }

}
