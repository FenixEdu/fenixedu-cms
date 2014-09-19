package org.fenixedu.bennu;

import javax.annotation.PostConstruct;

import org.fenixedu.bennu.portal.servlet.PortalBackendRegistry;
import org.fenixedu.bennu.spring.BennuSpringModule;
import org.fenixedu.cms.portal.CMSBean;
import org.fenixedu.cms.routing.CMSBackend;
import org.fenixedu.cms.routing.CMSEmbeddedBackend;
import org.fenixedu.cms.routing.CMSURLHandler;
import org.springframework.context.annotation.Bean;

@BennuSpringModule(basePackages = "org.fenixedu.cms", bundles = "CmsResources")
public class CMSConfiguration {

    @Bean
    public CMSBean cms() {
        return new CMSBean();
    }

    @Bean
    public CMSURLHandler cmsUrlHandler() {
        return new CMSURLHandler();
    }

    @Bean
    public CMSBackend cmsBackend() {
        return new CMSBackend(cmsUrlHandler());
    }

    @Bean
    public CMSEmbeddedBackend cmsEmbeddedBackend() {
        return new CMSEmbeddedBackend(cmsUrlHandler());
    }

    @PostConstruct
    public void initBackend() {
        PortalBackendRegistry.registerPortalBackend(cmsBackend());
        PortalBackendRegistry.registerPortalBackend(cmsEmbeddedBackend());
    }

}
