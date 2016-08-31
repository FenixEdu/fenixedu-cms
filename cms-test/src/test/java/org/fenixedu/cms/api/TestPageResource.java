package org.fenixedu.cms.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.cms.api.bean.PageBean;
import org.fenixedu.cms.domain.CmsTestUtils;
import org.fenixedu.cms.domain.Page;
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
public class TestPageResource extends TestCmsApi {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestPageResource.class);

    @Test
    public void getPage() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("getPage");
        Site site = CmsTestUtils.createSite(user, "getPage");
        Page page = CmsTestUtils.createPage(site, "getPage");

        // execute
        String response = getPageTarget(page).request().get(String.class);
        LOGGER.debug("getPage: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        // test
        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("response page should have an id field", jsonResponse.has("id"));
        assertEquals("response page should have same id as the created", page.getExternalId(), jsonResponse.get("id")
                .getAsString());

        assertTrue("response page should have a site field", jsonResponse.has("site"));
        assertEquals("site response should be equal to created site", site.getExternalId(), jsonResponse.get("site")
                .getAsString());

        assertTrue("response site should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", page.getCreationDate().toString(),
                jsonResponse.get("creationDate").getAsString());

        assertTrue("response site should have an modificationDate field", jsonResponse.has("modificationDate"));
        assertEquals("modificationDate response should be equal to expected", page.getModificationDate().toString(), jsonResponse
                .get("modificationDate").getAsString());

        assertTrue("response post should have a published field", jsonResponse.has("published"));
        assertEquals("published response should be equal to expected published", false, jsonResponse.get("published")
                .getAsBoolean());

        assertTrue("response site should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

        assertTrue("response page should have a name field", jsonResponse.has("name"));
        assertEquals("name response should be equal to expected name", page.getName(),
                LocalizedString.fromJson(jsonResponse.get("name").getAsJsonObject()));

        assertTrue("response page should have a slug field", jsonResponse.has("slug"));
        assertEquals("name response should be equal to expected name", page.getSlug(), jsonResponse.get("slug").getAsString());
    }

    @Test
    public void editPage() {
        // prepare
        User user = CmsTestUtils.createAuthenticatedUser("editPage");
        Site site = CmsTestUtils.createSite(user, "editPage");
        Page page = CmsTestUtils.createPage(site, "editPage");

        LocalizedString nameEdit =
                new LocalizedString(Locale.UK, "page name uk nameEdit").with(Locale.US, "page name us nameEdit");
        PageBean pageBean = new PageBean();
        pageBean.setName(nameEdit);
        pageBean.setSlug("editPage slug");
        pageBean.setPublished(false);

        // execute
        String response =
                getPageTarget(page).request().put(Entity.entity(pageBean.toJson(), MediaType.APPLICATION_JSON), String.class);
        // test
        LOGGER.debug("editPage: response = " + response.replaceAll("(\\r|\\n|\\t)", " "));

        assertTrue("should get page in response", !response.isEmpty() && !response.equals(EMPTY_RESPONSE));

        JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

        assertTrue("edit page should have an id field", jsonResponse.has("id"));
        assertEquals("edit page should have same id", page.getExternalId(), jsonResponse.get("id").getAsString());

        assertTrue("edited page should contain name field", jsonResponse.has("name"));
        assertEquals("edit page should have edited name", pageBean.getName(), LocalizedString.fromJson(jsonResponse.get("name")));

        assertTrue("response page should have a site field", jsonResponse.has("site"));
        assertEquals("site response should be equal to created site", site.getExternalId(), jsonResponse.get("site")
                .getAsString());

        assertTrue("edited post should contain name slug", jsonResponse.has("slug"));
        assertEquals("edit post should have edited slug", StringNormalizer.slugify(pageBean.getSlug()), jsonResponse.get("slug")
                .getAsString());

        assertTrue("edited post should contain published field", jsonResponse.has("published"));
        assertEquals("edit post should have edited published", pageBean.getPublished(), jsonResponse.get("published")
                .getAsBoolean());

        assertTrue("response site should have an creationDate field", jsonResponse.has("creationDate"));
        assertEquals("creationDate response should be equal to expected creationDate", page.getCreationDate().toString(),
                jsonResponse.get("creationDate").getAsString());

        assertTrue("response site should have an modificationDate field", jsonResponse.has("modificationDate"));
        assertEquals("modificationDate response should be equal to expected", page.getModificationDate().toString(), jsonResponse
                .get("modificationDate").getAsString());

        assertTrue("response site should have an createdBy field", jsonResponse.has("createdBy"));
        assertEquals("createdBy response should be equal to expected createdBy", user.getUsername(), jsonResponse
                .get("createdBy").getAsString());

    }

}
