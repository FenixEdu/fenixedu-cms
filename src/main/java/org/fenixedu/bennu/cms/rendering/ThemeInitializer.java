package org.fenixedu.bennu.cms.rendering;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

@HandlesTypes({ ThemeProvider.class })
public class ThemeInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> cs, ServletContext ctx) throws ServletException {
        for (Class c : cs) {
            try {
                ((ProvidesThemes) c.newInstance()).loadThemes();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
