package org.fenixedu.bennu.cms.domain;

import org.fenixedu.bennu.core.domain.User;

/**
 * Template file for a given {@link CMSTheme}
 */
public class CMSTemplateFile extends CMSTemplateFile_Base {
    
    public CMSTemplateFile(String displayName, String filename, byte[] content) {
        super();
        init(displayName, filename, content);
    }

    @Override
    public boolean isAccessible(User user) {
        // TODO Auto-generated method stub
        return true;
    }
    
}
