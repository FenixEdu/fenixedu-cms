package org.fenixedu.bennu.cms.rendering;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ThemeProvider {
    /**
     * The type of the component, this should be unique for the application.
     */
}

