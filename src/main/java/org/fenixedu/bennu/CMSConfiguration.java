package org.fenixedu.bennu;

import org.fenixedu.bennu.cms.portal.CMSBean;
import org.fenixedu.bennu.spring.BennuSpringModule;
import org.springframework.context.annotation.Bean;

@BennuSpringModule(basePackages = "org.fenixedu.cms", bundles = "CmsResources")
public class CMSConfiguration {

    @Bean
    public CMSBean cms() {
        return new CMSBean();
    }

}
