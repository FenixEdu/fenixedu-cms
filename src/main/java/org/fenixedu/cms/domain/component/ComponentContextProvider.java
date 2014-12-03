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

import java.util.Collection;
import java.util.Collections;

import org.fenixedu.cms.domain.Page;

/**
 * A {@link ComponentContextProvider} provides the slot options for {@link ComponentParameter} parameters.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
@FunctionalInterface
public interface ComponentContextProvider<T> {

    /**
     * Provide the contextual items for the given page.
     * 
     * @param page
     *            The page in which the component will be inserted.
     * @return
     *         The options for the given slot.
     */
    public Iterable<T> provide(Page page);

    /**
     * Presents the given item (returned by the {@link #provide(Page)} method).
     * By default, simply invokes {@link Object#toString()}.
     * 
     * @param item
     *            The item to present.
     * @return
     *         The presentation of the given item.
     */
    public default String present(T item) {
        return String.valueOf(item);
    }

    public static class EmptyProvider implements ComponentContextProvider<Object> {
        @Override
        public Collection<Object> provide(Page page) {
            return Collections.emptyList();
        }
    }

}
