package org.fenixedu.cms.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.cms.api.json.ThemeAdapter;
import org.fenixedu.cms.domain.CMSTheme;
import org.fenixedu.cms.domain.CmsTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RunWith(FenixFrameworkRunner.class)
public class TestThemeResource extends TestCmsApi {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestThemeResource.class);

    @Test
    public void getThemesEmpty() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("getThemesEmpty");

        // execute
        String response = getThemesTarget().request().get(String.class);
        LOGGER.debug("getThemesEmpty: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        //test
        assertTrue("By default shouldn't be any theme", response.equals(EMPTY_RESPONSE));
    }

    @Test
    public void getSeveralThemes() {
        // prepare
        Set<JsonElement> expectedJsonThemes = new HashSet<JsonElement>();

        User user = CmsTestUtils.createAuthenticatedUser("getSeveralThemes");

        CMSTheme theme1 = CmsTestUtils.createTheme("getSeveralThemes1");
        JsonElement theme1json = removeNullKeys(new ThemeAdapter().view(theme1, ctx));
        expectedJsonThemes.add(theme1json);

        CMSTheme theme2 = CmsTestUtils.createTheme("getSeveralThemes2");
        JsonElement theme2json = removeNullKeys(new ThemeAdapter().view(theme2, ctx));
        expectedJsonThemes.add(theme2json);

        // execute
        String response = getThemesTarget().request().get(String.class);
        LOGGER.debug("getSeveralThemes: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        //test
        assertTrue("themes list from site shouldn't be empty", !response.isEmpty() && !response.equals(EMPTY_RESPONSE));

        JsonArray jsonResponseArray = new JsonParser().parse(response).getAsJsonArray();
        assertTrue("response should contain two themes", jsonResponseArray.size() == 2);

        assertTrue("response should include theme1 and theme2", expectedJsonThemes.contains(jsonResponseArray.get(0))
                && expectedJsonThemes.contains(jsonResponseArray.get(1)));
    }

    @Test
    public void getTheme() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("getTheme");

        CMSTheme theme = CmsTestUtils.createTheme("getTheme");

        // execute
        String response = getThemeTarget(theme).request().get(String.class);
        LOGGER.debug("getTheme: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        //test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response theme should have an id field", jsonResponse.has("id"));
        assertEquals("response theme should have same id as the created", theme.getExternalId(), jsonResponse.get("id")
                .getAsString());

        assertTrue("response theme should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response theme should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", theme.getCreationDate().toString(),
                jsonResponse.get("creationDate").getAsString());

        assertTrue("response theme should have a name field", jsonResponse.has("name"));
        assertEquals("name response should be equal to expected name", theme.getName(), jsonResponse.get("name").getAsString());

        assertTrue("response theme should have a description field", jsonResponse.has("description"));
        assertEquals("description response should be equal to expected description", theme.getDescription(),
                jsonResponse.get("description").getAsString());

        assertTrue("response theme should have a type field", jsonResponse.has("type"));
        assertEquals("type response should be equal to expected type", theme.getType(), jsonResponse.get("type").getAsString());
    }

}
