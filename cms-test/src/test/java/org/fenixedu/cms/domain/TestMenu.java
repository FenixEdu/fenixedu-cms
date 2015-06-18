package org.fenixedu.cms.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;

@RunWith(FenixFrameworkRunner.class)
public class TestMenu extends TestCMS {

    @Test
    public void createMenu() {
        User user = CmsTestUtils.createAuthenticatedUser("createMenu");
        Site site = CmsTestUtils.createSite(user, "createMenu");

        String methodName = "createMenu";
        LocalizedString menuName =
                new LocalizedString(Locale.UK, "menu name uk " + methodName).with(Locale.US, "menu name us " + methodName);
        Menu menu = new Menu(site, menuName);
        DateTime dt = new DateTime();

        assertEquals(menuName, menu.getName());
        assertEquals("slug should be initialized using menu name", StringNormalizer.slugify(menuName.getContent()),
                menu.getSlug());
        assertTrue(equalDates(dt, menu.getCreationDate()));
        assertEquals(false, menu.getTopMenu());
        assertEquals(site, menu.getSite());
        assertTrue(menu.getItemsSet().isEmpty());
        assertTrue(menu.getToplevelItemsSet().isEmpty());
        assertEquals(user, menu.getCreatedBy());
    }

    @Test
    public void checkValidSlug() {
        User user = CmsTestUtils.createAuthenticatedUser("checkValidSlug");
        Site site = CmsTestUtils.createSite(user, "checkValidSlug");
        Menu menu1 = CmsTestUtils.createMenu(site, "checkValidSlug1");
        Menu menu2 = CmsTestUtils.createMenu(site, "checkValidSlug2");

        assertTrue(menu1.isValidSlug("xpto"));
        assertTrue(menu1.isValidSlug(menu1.getSlug()));
        assertFalse(menu1.isValidSlug(menu2.getSlug()));
    }
}
