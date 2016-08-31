package org.fenixedu.cms.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.cms.api.bean.CategoryBean;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.CmsTestUtils;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.commons.i18n.LocalizedString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RunWith(FenixFrameworkRunner.class)
public class TestCategoryResource extends TestCmsApi {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestCategoryResource.class);

    @Test
    public void getCategory() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("getCategory");
        Site site = CmsTestUtils.createSite(user, "getCategory");
        Category category = CmsTestUtils.createCategory(site, "getCategory");

        // execute
        String response = getCategoryTarget(category).request().get(String.class);
        LOGGER.debug("getCategory: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response category should have an id field", jsonResponse.has("id"));
        assertEquals("response category should have same id as the created", category.getExternalId(), jsonResponse.get("id")
                .getAsString());

        assertTrue("response category should have a site field", jsonResponse.has("site"));
        assertEquals("site response should be equal to created site", site.getExternalId(), jsonResponse.get("site")
                .getAsString());

        assertTrue("response category should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", category.getCreationDate().toString(),
                jsonResponse.get("creationDate").getAsString());

        assertTrue("response category should have a name field", jsonResponse.has("name"));
        assertEquals("name response should be equal to expected name", category.getName(),
                LocalizedString.fromJson(jsonResponse.get("name").getAsJsonObject()));

        assertTrue("response category should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response category should have a slug field", jsonResponse.has("slug"));
        assertEquals("slug response should be equal to expected slug", category.getSlug(), jsonResponse.get("slug").getAsString());
    }

    @Test
    public void editCategory() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("editCategory");
        Site site = CmsTestUtils.createSite(user, "editCategory");
        Category category = CmsTestUtils.createCategory(site, "getCategory");

        CategoryBean categoryBean = new CategoryBean();
        LocalizedString nameEdit =
                new LocalizedString(Locale.UK, "category name uk nameEdit").with(Locale.US, "category name us nameEdit");
        categoryBean.setName(nameEdit);
        categoryBean.setSlug(StringNormalizer.slugify("editCategory-slug"));

        // execute
        String response =
                getCategoryTarget(category).request().put(Entity.entity(categoryBean.toJson(), MediaType.APPLICATION_JSON),
                        String.class);

        // test
        LOGGER.debug("editCategory: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        assertTrue("should get category in response", !response.isEmpty() && !response.equals(EMPTY_RESPONSE));

        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("edit page should have an id field", jsonResponse.has("id"));
        assertEquals("edit page should have same id", category.getExternalId(), jsonResponse.get("id").getAsString());

        assertTrue("edited page should contain name field", jsonResponse.has("name"));
        assertEquals("edit page should have edited name", categoryBean.getName(),
                LocalizedString.fromJson(jsonResponse.get("name")));

        assertTrue("response page should have a site field", jsonResponse.has("site"));
        assertEquals("site response should be equal to created site", site.getExternalId(), jsonResponse.get("site")
                .getAsString());

        assertTrue("edited post should contain name slug", jsonResponse.has("slug"));
        assertEquals("edit post should have edited slug", StringNormalizer.slugify(categoryBean.getSlug()),
                jsonResponse.get("slug").getAsString());

        assertTrue("response site should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", category.getCreationDate().toString(),
                jsonResponse.get("creationDate").getAsString());

        assertTrue("response site should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

    }

}
