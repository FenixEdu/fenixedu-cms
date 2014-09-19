package org.fenixedu.cms.domain.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.fenixedu.cms.domain.component.ComponentContextProvider.EmptyProvider;

/**
 * Marks the annotated method parameter as being a dynamic parameter for the
 * given {@link ComponentType}.
 * 
 * This annotation may be applied to parameters of type:
 * 
 * <ul>
 * <li><strong>DomainObject</strong> - Will render a select box to choose from a list (a provider is required).</li>
 * <li><strong>Enum</strong> - Will render a select box with the various enum values (all if no provider is given).</li>
 * <li><strong>Boolean</strong> - Will render a checkbox</li>
 * <li><strong>String</strong> - Will render a text field, or a select box if a provider is given.</li>
 * <li><strong>Number</strong> - Will render a number field, or a select box if a provider is given.</li>
 * </ul>
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentParameter {

    /**
     * Returns the reference to an implementation of {@link ComponentContextProvider}, which will
     * be used to give the user a choice of value.
     * 
     * Do not change this value if you wish the user to be free to specify any value.
     * 
     * @return
     *         The provider class
     */
    public Class<? extends ComponentContextProvider<?>> provider() default EmptyProvider.class;

    /**
     * Returns the presentation name of this parameter.
     * 
     * @return
     *         The presentation name
     */
    public String value();

    /**
     * Returns whether the annotated parameter is required.
     * 
     * If it is not required, a null value may be passed to the constructor parameter.
     * 
     * @return
     *         Whether this parameter is required
     */
    public boolean required() default true;

}
