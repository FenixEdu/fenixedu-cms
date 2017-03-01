package org.fenixedu.cms.domain;


import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.Group;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;

import static org.junit.Assert.*;

/**
 * Created by diutsu on 06/03/17.
 */
@RunWith(FenixFrameworkRunner.class)
public class TestSiteBuilder extends TestCMS {
  
    @Test
    public void createBuilder(){
        String slug = "builder-creation";
    
        SiteBuilder builder = new SiteBuilder(slug);
        
        assertEquals(builder.getSlug(),slug);
        assertTrue(builder.getRoleTemplateSet().isEmpty());
        assertTrue(builder.getCategoriesSet().isEmpty());
        assertEquals(builder.getCanViewGroup(), Group.nobody());
        assertNull(builder.getFolder());
        assertFalse(builder.getPublished());
        assertFalse(builder.isSystemBuilder());
        assertNull(builder.getTheme());
        assertTrue(Bennu.getInstance().getSiteBuildersSet().contains(builder));
    
    }
    
}
