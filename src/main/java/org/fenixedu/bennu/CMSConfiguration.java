package org.fenixedu.bennu;

import javax.annotation.PostConstruct;

import org.fenixedu.bennu.cms.portal.CMSBean;
import org.fenixedu.bennu.cms.routing.CMSBackend;
import org.fenixedu.bennu.cms.routing.CMSURLHandler;
import org.fenixedu.bennu.portal.servlet.PortalBackendRegistry;
import org.fenixedu.bennu.spring.BennuSpringModule;
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

    @PostConstruct
    public void initBackend() {
        PortalBackendRegistry.registerPortalBackend(cmsBackend());
    }

}
