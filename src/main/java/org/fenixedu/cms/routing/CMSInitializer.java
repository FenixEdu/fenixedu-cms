package org.fenixedu.cms.routing;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.bennu.portal.servlet.PortalBackendRegistry;

@WebListener
public class CMSInitializer implements ServletContextListener{

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        
        
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        PortalBackendRegistry.registerPortalBackend(new CMSBackend());
    }
    
}
