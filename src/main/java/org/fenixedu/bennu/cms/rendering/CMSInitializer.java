package org.fenixedu.bennu.cms.rendering;

import java.io.InputStream;
import java.util.Set;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import org.fenixedu.bennu.cms.domain.CMSThemeLoader;
import org.fenixedu.bennu.cms.domain.RegisterSiteTemplate;
import org.fenixedu.bennu.cms.domain.Site;
import org.fenixedu.bennu.cms.domain.component.Component;
import org.fenixedu.bennu.cms.domain.component.ComponentType;

@HandlesTypes({ ComponentType.class, RegisterSiteTemplate.class })
public class CMSInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        if (c != null) {
            for (Class<?> type : c) {
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
        registerDefaultTheme(ctx);
    }

    private void registerDefaultTheme(ServletContext ctx) {
        InputStream in = ctx.getResourceAsStream("/WEB-INF/cms-default-theme.zip");
        ZipInputStream zin = new ZipInputStream(in);
        CMSThemeLoader.createFromZipStream(zin);
    }

}
