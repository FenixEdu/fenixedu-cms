package org.fenixedu.cms.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;

@RunWith(FenixFrameworkRunner.class)
public class TestPage extends TestCMS {

    @Test
    public void createPage() {
        User user = CmsTestUtils.createAuthenticatedUser("createPage");
        Site site = CmsTestUtils.createSite(user, "createPage");

        String methodName = "createPage";
        LocalizedString pageName =
                new LocalizedString(Locale.UK, "page name uk " + methodName).with(Locale.US, "page name us " + methodName);
        Page page = new Page(site, pageName);
        DateTime dt = new DateTime();

        assertEquals(pageName, page.getName());
        assertEquals("slug should be initialized using page name", StringNormalizer.slugify(pageName.getContent()),
                page.getSlug());
        assertEquals(null, page.getTemplateType());
        assertEquals(false, page.getPublished());
        assertTrue(equalDates(dt, page.getCreationDate()));
        assertTrue(equalDates(dt, page.getModificationDate()));
        assertEquals(user, page.getCreatedBy());
        assertEquals(site, page.getSite());
        assertTrue(page.getComponentsSet().isEmpty());
        assertTrue(page.getMenuItemsSet().isEmpty());
        assertEquals(null, page.getTemplateType());
        assertEquals(null, page.getTemplate());
        assertFalse(page.getStaticPost().isPresent());
        assertEquals(CoreConfiguration.getConfiguration().applicationUrl() + "/" + site.getBaseUrl() + "/" + page.getSlug(),
                page.getAddress());
    }

    @Test
    public void checkValidSlug() {
        User user = CmsTestUtils.createAuthenticatedUser("checkValidSlug");
        Site site = CmsTestUtils.createSite(user, "checkValidSlug");
        Page page1 = CmsTestUtils.createPage(site, "checkValidSlug1");
        Page page2 = CmsTestUtils.createPage(site, "checkValidSlug2");

        assertTrue(page1.isValidSlug("xpto"));
        assertTrue(page1.isValidSlug(page1.getSlug()));
        assertFalse(page1.isValidSlug(page2.getSlug()));
    }
}
