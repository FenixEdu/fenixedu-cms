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
