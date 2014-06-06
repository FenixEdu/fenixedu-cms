package org.fenixedu.bennu.cms.routing;

import org.fenixedu.bennu.portal.servlet.PortalBackend;
import org.fenixedu.bennu.portal.servlet.SemanticURLHandler;


public class CMSBackend implements PortalBackend {

    public static final String BACKEND_KEY = "cms";
    
    @Override
    public String getBackendKey() {
        return BACKEND_KEY;
    }

    @Override
    public SemanticURLHandler getSemanticURLHandler() {
        return new CMSURLHandler();
    }

    @Override
    public boolean requiresServerSideLayout() {
        // TODO Auto-generated method stub
        return false;
    }

}
