package org.fenixedu.bennu.cms.routing;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.bennu.cms.domain.CMSThemeLoader;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.portal.servlet.PortalBackendRegistry;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@WebListener
public class CMSInitializer implements ServletContextListener{

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        
        
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        PortalBackendRegistry.registerPortalBackend(new CMSBackend());
        ensureCMSTheme();
    }

    @Atomic(mode = TxMode.SPECULATIVE_READ)
    private void ensureCMSTheme() {
        Bennu bennu = Bennu.getInstance();
        if (bennu.getCMSThemesSet().isEmpty() || bennu.getDefaultCMSTheme() == null) {
                CMSThemeLoader.createDefaultThemes();
        }
    }
    
}
