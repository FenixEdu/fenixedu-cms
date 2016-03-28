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
package org.fenixedu.cms.rendering;

import org.fenixedu.cms.domain.CMSTheme;
import org.fenixedu.cms.domain.RegisterSiteTemplate;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.component.ComponentType;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.util.Set;

@HandlesTypes({ ComponentType.class, RegisterSiteTemplate.class })
public class CMSInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        if (c != null) {
            for (Class<?> type : c) {
                
            	if (type.isAnnotationPresent(ComponentType.class)) {
                    Component.register(type);
                }
                
                if (type.isAnnotationPresent(RegisterSiteTemplate.class)) {
                    RegisterSiteTemplate annotation = type.getAnnotation(RegisterSiteTemplate.class);
                    Site.register(annotation.type(), type);
                }
            }
        }
        registerDefaultTheme(ctx);
    }

    private void registerDefaultTheme(ServletContext ctx) {
        CMSTheme.loadDefaultTheme();
    }

}
