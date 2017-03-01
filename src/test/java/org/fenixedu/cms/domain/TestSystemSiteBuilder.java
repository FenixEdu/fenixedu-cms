package org.fenixedu.cms.domain;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.Group;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * Created by diutsu on 06/03/17.
 */
@RunWith(FenixFrameworkRunner.class)
public class TestSystemSiteBuilder extends TestCMS {
    
    @Test
    public void testBlogCreation(){
        BlogSiteBuilder builder = BlogSiteBuilder.getInstance();
    
        assertTrue(builder.isSystemBuilder());
        assertEquals(builder.getSlug(),BlogSiteBuilder.class.getSimpleName());
        assertTrue(builder.isSystemBuilder());
        assertTrue(builder.getRoleTemplateSet().isEmpty());
        assertTrue(builder.getCategoriesSet().isEmpty());
        assertEquals(builder.getCanViewGroup(), Group.nobody());
        assertNull(builder.getFolder());
        assertFalse(builder.getPublished());
        assertNull(builder.getTheme());
        assertTrue(Bennu.getInstance().getSiteBuildersSet().contains(builder));
    }
    
}
