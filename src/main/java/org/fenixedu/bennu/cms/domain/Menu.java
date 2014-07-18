package org.fenixedu.bennu.cms.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Model of a Menu for a given {@link Page}
 */
public class Menu extends Menu_Base {

    public Menu(Site site, LocalizedString name) {
        this();
        setSite(site);
        setName(name);
        setTopMenu(false);
    }

    /**
     * Logged {@link User} creates a new Menu.
     */
    public Menu() {
        super();
        if (Authenticate.getUser() == null) {
            throw new RuntimeException("Needs Login");
        }
        this.setCreatedBy(Authenticate.getUser());
        this.setCreationDate(new DateTime());
    }

    @Atomic
    public void delete() {
        Sets.newHashSet(getComponentSet()).forEach(c -> c.delete());
        Sets.newHashSet(getItemsSet()).forEach(i -> i.delete());
        this.setCreatedBy(null);
        this.setSite(null);
        this.deleteDomainObject();
    }

    /**
     * Puts a {@link MenuItem} at a given position, shifting the existing ones to the right.
     * 
     * @param item
     *            The {@link MenuItem} to be added.
     * @param position
     *            the position to save the item.
     */
    public void putAt(MenuItem item, int position) {
        if (position < 0) {
            position = 0;
        }

        if (position >= this.getToplevelItemsSet().size()) {
            item.removeFromParent();
            position = getToplevelItemsSorted().size();
        }

        if (item.getPosition() != null) {
            item.removeFromParent();
        }

        List<MenuItem> list = Lists.newArrayList(getToplevelItemsSorted());
        list.add(position, item);

        MenuItem.fixOrder(list);

        getToplevelItemsSet().add(item);
        getItemsSet().add(item);
    }

    /**
     * Removes a given {@link MenuItem} from the Menu.
     * 
     * @param mi
     *            the {@link MenuItem} to be removed.
     */
    public void remove(MenuItem mi) {
        getToplevelItemsSet().remove(mi);
        MenuItem.fixOrder(getToplevelItemsSorted());

        getItemsSet().remove(mi);
        MenuItem.fixOrder(getItemsSorted());
    }

    /**
     * Adds a given {@link MenuItem} as the last item.
     * 
     * @param mi
     *            the {@link MenuItem} to be added.
     */
    public void add(MenuItem mi) {
        this.putAt(mi, getToplevelItemsSet().size());
    }

    /**
     * @return the menu items sorted by position.
     */
    public List<MenuItem> getChildrenSorted() {
        return getToplevelItemsSorted();
    }

    public List<MenuItem> getToplevelItemsSorted() {
        return getToplevelItemsSet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }

    public List<MenuItem> getItemsSorted() {
        return getItemsSet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }

    public <T> Set<T> getComponentsOfClass(Class<T> clazz) {
        return Sets.newHashSet(Iterables.filter(getComponentSet(), clazz));
    }
}
