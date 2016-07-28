package org.fenixedu.cms.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.cms.api.bean.CategoryBean;
import org.fenixedu.cms.api.json.CategoryAdapter;
import org.fenixedu.cms.domain.Category;
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

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(FenixFrameworkRunner.class)
public class TestSiteCategoryResource extends TestCmsApi {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestSiteCategoryResource.class);

    @Test
    public void getSiteCategoriesEmpty() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("getSiteCategoriesEmpty");

        Site site = CmsTestUtils.createSite(user, "getSiteCategoriesEmpty");

        // execute
        String response = getSiteCategoriesTarget(site).request().get(String.class);
        LOGGER.debug("getSiteCategoriesEmpty: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        //test
        assertTrue("New sites shouldn't have any categories yet", response.equals(EMPTY_RESPONSE));
    }

    @Test
    public void getSiteSeveralCategories() {
        // prepare
        Set<JsonElement> expectedJsonCategories = new HashSet<JsonElement>();

        User user = CmsTestUtils.createAuthenticatedUser("getSiteSeveralCategories");

        Site site = CmsTestUtils.createSite(user, "getSiteSeveralCategories");

        Category category1 = CmsTestUtils.createCategory(site, "getSiteSeveralCategories1");
        JsonElement category1json = removeNullKeys(new CategoryAdapter().view(category1, ctx));
        expectedJsonCategories.add(category1json);

        Category category2 = CmsTestUtils.createCategory(site, "getSiteSeveralCategories2");
        JsonElement category2json = removeNullKeys(new CategoryAdapter().view(category2, ctx));
        expectedJsonCategories.add(category2json);

        // execute
        String response = getSiteCategoriesTarget(site).request().get(String.class);
        LOGGER.debug("getSiteSeveralCategories: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        //test
        assertTrue("category list from site shouldn't be empty", !response.isEmpty() && !response.equals(EMPTY_RESPONSE));

        JsonArray jsonResponseArray = new JsonParser().parse(response).getAsJsonArray();
        assertTrue("response should contain two categories", jsonResponseArray.size() == 2);

        assertTrue("response should include category1 and category2", expectedJsonCategories.contains(jsonResponseArray.get(0))
                && expectedJsonCategories.contains(jsonResponseArray.get(1)));
    }

    @Test
    public void createNoNameCategory() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("createNoNameCategory");

        Site site = CmsTestUtils.createSite(user, "createNoNameCategory");

        CategoryBean categoryBean = new CategoryBean();

        DateTime creationDate = new DateTime();

        // execute
        try {
            String response = getSiteCategoriesTarget(site).request()
                .post(Entity.entity(categoryBean.toJson(), MediaType.APPLICATION_JSON), String.class);
        } catch (BadRequestException ex) {
            LOGGER.debug(ex.getMessage());
        }
    }


    @Test
    public void createMinCategory() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("createMinCategory");

        Site site = CmsTestUtils.createSite(user, "createMinCategory");

        CategoryBean categoryBean = new CategoryBean();
        LocalizedString name =
            new LocalizedString(Locale.UK, "createFullCategory-name-uk").with(Locale.US, "createFullCategory-name-us");
        categoryBean.setName(name);

        DateTime creationDate = new DateTime();

        // execute
        String response = getSiteCategoriesTarget(site).request()
            .post(Entity.entity(categoryBean.toJson(), MediaType.APPLICATION_JSON), String.class);

        // test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response category should have an id field", jsonResponse.has("id"));
        Category category = FenixFramework.getDomainObject(jsonResponse.get("id").getAsString());
        assertTrue("create endpoint should return id of an existing category", category != null);

        assertTrue("response category should have a site field", jsonResponse.has("site"));
        assertEquals("site response should be equal to created site", site.getExternalId(), jsonResponse.get("site")
                .getAsString());

        assertTrue("response category should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", creationDate.toString().substring(0, 16),
                jsonResponse // 16 to compare only date and time (hours and minutes) YYYY-MM-DD hh:mm
                .get("creationDate").getAsString().substring(0, 16));

        assertTrue("response category should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response category should  contain slug", jsonResponse.has("slug"));

        assertTrue("response category should  contain name", jsonResponse.has("name"));
        assertEquals("name response should be equal to expected name", name,
            LocalizedString.fromJson(jsonResponse.get("name").getAsJsonObject()));
    }

    @Test
    public void createFullCategory() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("createFullCategory");

        Site site = CmsTestUtils.createSite(user, "createFullCategory");

        CategoryBean categoryBean = new CategoryBean();
        LocalizedString name =
                new LocalizedString(Locale.UK, "createFullCategory-name-uk").with(Locale.US, "createFullCategory-name-us");
        categoryBean.setName(name);
        categoryBean.setSlug(StringNormalizer.slugify("createFullPage-slug"));

        DateTime creationDate = new DateTime();

        // execute
        String response =
                getSiteCategoriesTarget(site).request().post(Entity.entity(categoryBean.toJson(), MediaType.APPLICATION_JSON),
                        String.class);
        LOGGER.debug("createFullCategory: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response category should have an id field", jsonResponse.has("id"));
        Category category = FenixFramework.getDomainObject(jsonResponse.get("id").getAsString());
        assertTrue("create endpoint should return id of an existing category", category != null);

        assertTrue("response category should have a site field", jsonResponse.has("site"));
        assertEquals("site response should be equal to created site", site.getExternalId(), jsonResponse.get("site")
                .getAsString());

        assertTrue("response category should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", creationDate.toString().substring(0, 16),
                jsonResponse // 16 to compare only date and time (hours and minutes) YYYY-MM-DD hh:mm
                .get("creationDate").getAsString().substring(0, 16));

        assertTrue("response category should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response category should have a name field", jsonResponse.has("name"));
        assertEquals("name response should be equal to expected name", name,
                LocalizedString.fromJson(jsonResponse.get("name").getAsJsonObject()));

        assertTrue("response category should have a slug field", jsonResponse.has("slug"));
        assertEquals("slug response should be equal to expected slug", categoryBean.getSlug(), jsonResponse.get("slug")
                .getAsString());
    }
}
