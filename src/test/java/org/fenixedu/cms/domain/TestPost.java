package org.fenixedu.cms.domain;

import org.fenixedu.bennu.core.domain.User;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;

import static org.junit.Assert.*;

@RunWith(FenixFrameworkRunner.class)
public class TestPost extends TestCMS {

    @Test
    public void createPost() {
        User user = CmsTestUtils.createAuthenticatedUser("createPost");
        Site site = CmsTestUtils.createSite(user, "createPost");

        Post post = new Post(site);
        DateTime dt = new DateTime();

        assertEquals(null, post.getName());
        assertEquals(null, post.getSlug());
        assertEquals(null, post.getPublicationBegin());
        assertEquals(null, post.getPublicationEnd());
        assertEquals(null, post.getLocation());
        assertEquals(null, post.getMetadata());
        assertEquals(false, post.getActive());
        assertTrue(equalDates(dt, post.getCreationDate()));
        assertTrue(equalDates(dt, post.getModificationDate()));
        assertEquals(user, post.getCreatedBy());
        assertEquals(site, post.getSite());
        assertTrue(post.getCategoriesSet().isEmpty());
        assertTrue(post.getComponentSet().isEmpty());
        assertTrue(post.getFilesSet().isEmpty());
        assertTrue(post.getRevisionsSet().isEmpty());
    }

    @Test
    public void checkValidSlug() {
        User user = CmsTestUtils.createAuthenticatedUser("checkValidSlug");
        Site site = CmsTestUtils.createSite(user, "checkValidSlug");
        Post post1 = CmsTestUtils.createPost(site, "checkValidSlug1");
        Post post2 = CmsTestUtils.createPost(site, "checkValidSlug2");

        assertTrue(post1.isValidSlug("xpto"));
        assertTrue(post1.isValidSlug(post1.getSlug()));
        assertFalse(post1.isValidSlug(post2.getSlug()));
    }

    @Test
    public void hasPublicationPeriod() {
        User user = CmsTestUtils.createAuthenticatedUser("hasPublicationPeriod");
        Site site = CmsTestUtils.createSite(user, "hasPublicationPeriod");
        Post post = CmsTestUtils.createPost(site, "hasPublicationPeriod");

        assertEquals(null, post.getPublicationBegin());
        assertEquals(null, post.getPublicationEnd());
        assertFalse(post.hasPublicationPeriod());

        DateTime dt = new DateTime();
        post.setPublicationBegin(dt);

        assertEquals(dt, post.getPublicationBegin());
        assertEquals(null, post.getPublicationEnd());
        assertFalse(post.hasPublicationPeriod());

        post.setPublicationBegin(null);
        post.setPublicationEnd(dt);

        assertEquals(null, post.getPublicationBegin());
        assertEquals(dt, post.getPublicationEnd());
        assertFalse(post.hasPublicationPeriod());

        post.setPublicationBegin(dt);

        assertEquals(dt, post.getPublicationBegin());
        assertEquals(dt, post.getPublicationEnd());
        assertTrue(post.hasPublicationPeriod());
    }

    @Test
    public void inPublicationPeriod() {
        /*
         * publicationBegin and publicationEnd may have 3 values each:
         * null,
         * a date before now,
         * a date after now.
         *
         * need to check 3x3 combinations
         */

        User user = CmsTestUtils.createAuthenticatedUser("inPublicationPeriod");
        Site site = CmsTestUtils.createSite(user, "inPublicationPeriod");
        Post post = CmsTestUtils.createPost(site, "inPublicationPeriod");

        assertEquals(null, post.getPublicationBegin());
        assertEquals(null, post.getPublicationEnd());
        assertTrue(post.isInPublicationPeriod());

        DateTime before = new DateTime(2000, 1, 1, 1, 1);
        DateTime after = new DateTime(2050, 1, 1, 1, 1);

        post.setPublicationBegin(before);

        assertEquals(before, post.getPublicationBegin());
        assertEquals(null, post.getPublicationEnd());
        assertTrue(post.isInPublicationPeriod());

        post.setPublicationBegin(after);

        assertEquals(after, post.getPublicationBegin());
        assertEquals(null, post.getPublicationEnd());
        assertFalse(post.isInPublicationPeriod());

        post.setPublicationBegin(null);
        post.setPublicationEnd(before);

        assertEquals(null, post.getPublicationBegin());
        assertEquals(before, post.getPublicationEnd());
        assertFalse(post.isInPublicationPeriod());

        post.setPublicationBegin(before);

        assertEquals(before, post.getPublicationBegin());
        assertEquals(before, post.getPublicationEnd());
        assertFalse(post.isInPublicationPeriod());

        post.setPublicationBegin(after);

        assertEquals(after, post.getPublicationBegin());
        assertEquals(before, post.getPublicationEnd());
        assertFalse(post.isInPublicationPeriod());

        post.setPublicationBegin(null);
        post.setPublicationEnd(after);

        assertEquals(null, post.getPublicationBegin());
        assertEquals(after, post.getPublicationEnd());
        assertTrue(post.isInPublicationPeriod());

        post.setPublicationBegin(before);

        assertEquals(before, post.getPublicationBegin());
        assertEquals(after, post.getPublicationEnd());
        assertTrue(post.isInPublicationPeriod());

        post.setPublicationBegin(after);

        assertEquals(after, post.getPublicationBegin());
        assertEquals(after, post.getPublicationEnd());
        assertFalse(post.isInPublicationPeriod());
    }

    @Test
    public void visibility() {
        /*
         * isVisible depends on 3 boolean values:
         * getActive
         * hasPublicationPeriod
         * isInPublicationPeriod
         *
         * need to check 2x2x2 combinations
         */
        User user = CmsTestUtils.createAuthenticatedUser("visibility");
        Site site = CmsTestUtils.createSite(user, "visibility");
        Post post = CmsTestUtils.createPost(site, "visibility");

        DateTime before = new DateTime(2000, 1, 1, 1, 1);
        DateTime after = new DateTime(2050, 1, 1, 1, 1);

        post.setPublicationBegin(after);

        assertFalse(post.getActive());
        assertFalse(post.hasPublicationPeriod());
        assertFalse(post.isInPublicationPeriod());
        assertFalse(post.isVisible());

        post.setPublicationBegin(null);

        assertFalse(post.getActive());
        assertFalse(post.hasPublicationPeriod());
        assertTrue(post.isInPublicationPeriod());
        assertFalse(post.isVisible());

        post.setPublicationBegin(after);
        post.setPublicationEnd(before);

        assertFalse(post.getActive());
        assertTrue(post.hasPublicationPeriod());
        assertFalse(post.isInPublicationPeriod());
        assertFalse(post.isVisible());

        post.setPublicationBegin(before);
        post.setPublicationEnd(after);

        assertFalse(post.getActive());
        assertTrue(post.hasPublicationPeriod());
        assertTrue(post.isInPublicationPeriod());
        assertFalse(post.isVisible());

        post.setActive(true);
        post.setPublicationBegin(after);
        post.setPublicationEnd(null);

        assertTrue(post.getActive());
        assertFalse(post.hasPublicationPeriod());
        assertFalse(post.isInPublicationPeriod());
        assertTrue(post.isVisible());

        post.setPublicationBegin(null);

        assertTrue(post.getActive());
        assertFalse(post.hasPublicationPeriod());
        assertTrue(post.isInPublicationPeriod());
        assertTrue(post.isVisible());

        post.setPublicationBegin(after);
        post.setPublicationEnd(before);

        assertTrue(post.getActive());
        assertTrue(post.hasPublicationPeriod());
        assertFalse(post.isInPublicationPeriod());
        assertFalse(post.isVisible());

        post.setPublicationBegin(before);
        post.setPublicationEnd(after);

        assertTrue(post.getActive());
        assertTrue(post.hasPublicationPeriod());
        assertTrue(post.isInPublicationPeriod());
        assertTrue(post.isVisible());
    }
}
