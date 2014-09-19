package org.fenixedu.cms.routing;

import org.fenixedu.bennu.portal.servlet.PortalBackend;
import org.fenixedu.bennu.portal.servlet.SemanticURLHandler;

public class CMSEmbeddedBackend implements PortalBackend {

    public static final String BACKEND_KEY = "cms-embedded";

    private final CMSURLHandler handler;

    public CMSEmbeddedBackend(CMSURLHandler handler) {
        this.handler = handler;
    }

    @Override
    public SemanticURLHandler getSemanticURLHandler() {
        return handler;
    }

    @Override
    public boolean requiresServerSideLayout() {
        return true;
    }

    @Override
    public String getBackendKey() {
        return BACKEND_KEY;
    }

}
