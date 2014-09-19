package org.fenixedu.cms.domain.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the annotated constructor as being the dynamic constructor
 * for a {@link Component} subclass.
 * 
 * Note that all the parameters of the annotated method MUST be annotated with {@link ComponentParameter}.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicComponent {

}
