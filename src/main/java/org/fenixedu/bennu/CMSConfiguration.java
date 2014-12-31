/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of FenixEdu CMS.
 *
 * FenixEdu CMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu CMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu CMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu;

import javax.annotation.PostConstruct;

import org.fenixedu.bennu.portal.servlet.PortalBackendRegistry;
import org.fenixedu.bennu.spring.BennuSpringModule;
import org.fenixedu.cms.routing.CMSBackend;
import org.fenixedu.cms.routing.CMSEmbeddedBackend;
import org.fenixedu.cms.routing.CMSURLHandler;
import org.fenixedu.cms.ui.CMSBean;
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
