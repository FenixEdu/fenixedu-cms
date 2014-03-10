package org.fenixedu.cms.domain;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

@HandlesTypes({ RegisterSiteTemplate.class })
public class TemplateCollector implements ServletContainerInitializer{

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        for(Class i : c){
            RegisterSiteTemplate annotation = (RegisterSiteTemplate) i.getAnnotation(RegisterSiteTemplate.class);
            Site.register(annotation.type(), i);
        }
    }

}
