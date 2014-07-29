package org.fenixedu.bennu.cms.rendering;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import org.fenixedu.bennu.cms.domain.Component;
import org.fenixedu.bennu.cms.domain.ComponentType;
import org.fenixedu.bennu.cms.domain.RegisterSiteTemplate;
import org.fenixedu.bennu.cms.domain.Site;

@HandlesTypes({ ThemeProvider.class, ComponentType.class, RegisterSiteTemplate.class })
public class CMSInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        if (c != null) {
            for (Class<?> type : c) {
                if (ThemeProvider.class.isAssignableFrom(type)) {
                    try {
                        ThemeProvider provider = (ThemeProvider) type.newInstance();
                        provider.registerThemes(ctx);
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new ServletException(e);
                    }
                }
                if (type.isAnnotationPresent(ComponentType.class)) {
                    ComponentType annotation = type.getAnnotation(ComponentType.class);
                    Component.register(annotation.type(), type);
                }
                if (type.isAnnotationPresent(RegisterSiteTemplate.class)) {
                    RegisterSiteTemplate annotation = type.getAnnotation(RegisterSiteTemplate.class);
                    Site.register(annotation.type(), type);
                }
            }
        }
    }

}
