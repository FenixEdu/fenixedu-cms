package org.fenixedu.bennu.cms.domain.component;

import java.util.Collection;
import java.util.Collections;

import org.fenixedu.bennu.cms.domain.Page;

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
