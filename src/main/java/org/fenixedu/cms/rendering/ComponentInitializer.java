package org.fenixedu.cms.rendering;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import org.fenixedu.cms.domain.Component;
import org.fenixedu.cms.domain.ComponentType;

@HandlesTypes({ ComponentType.class })
public class ComponentInitializer implements ServletContainerInitializer{

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        for(Class i : c){
            ComponentType annotation = (ComponentType) i.getAnnotation(ComponentType.class);
            Component.register(annotation.type(), i);
        }
    }

}
