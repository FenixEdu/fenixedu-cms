package org.fenixedu.bennu.cms.domain.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentType {
    /**
     * The type of the component, this should be unique for the application.
     */
    String type();

    String name();

    String description();
}
