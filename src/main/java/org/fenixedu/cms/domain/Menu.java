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
package org.fenixedu.cms.domain;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.domain.wraps.Wrap;
import org.fenixedu.cms.domain.wraps.Wrappable;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

import com.google.common.collect.Sets;

/**
 * Model of a Menu for a given {@link Page}
 */
public class Menu extends Menu_Base implements Wrappable {

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
            throw CmsDomainException.forbiden();
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
            position = getToplevelItemsSet().size();
        }

        if (item.getPosition() != null) {
            item.removeFromParent();
        }

        List<MenuItem> list = getToplevelItemsSorted().collect(Collectors.toList());
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
        MenuItem.fixOrder(getToplevelItemsSorted().collect(Collectors.toList()));

        getItemsSet().remove(mi);
        MenuItem.fixOrder(getItemsSorted().collect(Collectors.toList()));
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

    public Stream<MenuItem> getToplevelItemsSorted() {
        return getToplevelItemsSet().stream().sorted();
    }

    public Stream<MenuItem> getItemsSorted() {
        return getItemsSet().stream().sorted();
    }

    @SuppressWarnings("unused")
    private class MenuWrap extends Wrap {
        private final Page page;
        private final Stream<Wrap> children;

        public MenuWrap() {
            this.page = null;
            this.children = Stream.empty();
        }

        public MenuWrap(Page page) {
            this.page = page;
            this.children = getToplevelItemsSorted().filter(MenuItem::isVisible).map(item -> item.makeWrap(page));
        }

        public Stream<Wrap> getChildren() {
            return children;
        }

        public LocalizedString getName() {
            return Menu.this.getName();
        }

        public Wrap getSite() {
            return Menu.this.getSite().makeWrap();
        }

        public Boolean getTopMenu() {
            return Menu.this.getTopMenu();
        }
    }

    @Override
    public Wrap makeWrap() {
        return new MenuWrap();
    }

    public Wrap makeWrap(Page page) {
        return new MenuWrap(page);
    }
}
