package org.fenixedu.bennu.cms.routing;

import org.fenixedu.bennu.portal.servlet.PortalBackend;
import org.fenixedu.bennu.portal.servlet.SemanticURLHandler;

public class CMSBackend implements PortalBackend {

    public static final String BACKEND_KEY = "cms";

    private final CMSURLHandler handler = new CMSURLHandler();

    @Override
    public String getBackendKey() {
        return BACKEND_KEY;
    }

    @Override
    public SemanticURLHandler getSemanticURLHandler() {
        return handler;
    }

    @Override
    public boolean requiresServerSideLayout() {
        return false;
    }

}
