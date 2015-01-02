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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.domain.wraps.Wrap;
import org.fenixedu.cms.domain.wraps.Wrappable;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.consistencyPredicates.ConsistencyPredicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Models the items of a {@link Menu}
 */
public class MenuItem extends MenuItem_Base implements Comparable<MenuItem>, Wrappable {

    /**
     * The logged {@link User} creates a new MenuItem.
     */
    public MenuItem(Menu menu) {
        super();
        if (Authenticate.getUser() == null) {
            throw CmsDomainException.forbiden();
        }
        this.setCreatedBy(Authenticate.getUser());
        this.setCreationDate(new DateTime());
        this.setFolder(false);
        this.setMenu(menu);
    }
    
    public Menu getMenu(){
        return super.getMenu();
    }

    /**
     * Adds a children at a given position and shifts the existing items.
     *
     * @param item the {@link MenuItem} to be added.
     * @param position the position where the item should be added.
     */
    public void putAt(MenuItem item, int position) {

        if (item.getPosition() != null) {
            item.removeFromParent();
        }

        if (position < 0) {
            position = 0;
        } else if (position > getChildrenSet().size()) {
            item.removeFromParent();
            position = getChildrenSet().size();
        }

        List<MenuItem> list = Lists.newArrayList(getChildrenSorted());
        list.add(position, item);

        fixOrder(list);

        getChildrenSet().add(item);
    }

    /**
     * Removes a given {@link MenuItem}
     *
     * @param mi the children to be removed
     */
    public void remove(MenuItem mi) {
        ArrayList<MenuItem> items = Lists.newArrayList(getChildrenSorted());
        items.remove(mi);
        fixOrder(items);
        removeChildren(mi);
    }

    /**
     * Adds a new {@link MenuItem} has the last item.
     *
     * @param mi the {@link MenuItem} to be added.
     */
    public void add(MenuItem mi) {
        this.putAt(mi, getChildrenSet().size());
    }

    /**
     * Removes the {@link MenuItem} from its parent.
     * <p>
     * The Parent can be a {@link Menu} or a {@link MenuItem}
     * </p>
     */
    public void removeFromParent() {
        if (this.getTop() != null) {
            this.getTop().remove(this);
            this.setTop(null);
        }
        if (this.getParent() != null) {
            this.getParent().remove(this);
            this.setParent(null);
        }
    }

    /**
     * @return the childrens sorted by position
     */
    public List<MenuItem> getChildrenSorted() {
        return getChildrenSet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }

    /**
     * @return the URL address to visit the item.
     */
    public String getAddress() {
        if (getFolder()) {
            return "#";
        }
        if (getUrl() != null) {
            return getUrl();
        } else {
            return getPage().getAddress();
        }
    }

    /**
     * A MenuItem can not be linked with a {@link Menu} and a {@link MenuItem} at the same time
     */
    @ConsistencyPredicate
    public boolean parentOrTop() {
        return !(getTop() != null && getParent() != null);
    }

    @Atomic
    public void delete() {
        List<MenuItem> items = Lists.newArrayList(getChildrenSet());
        removeFromParent();

        items.forEach(i -> remove(i));

        this.setCreatedBy(null);
        this.setMenu(null);
        this.setPage(null);

        this.deleteDomainObject();
    }

    @Override
    public int compareTo(MenuItem o) {
        return getPosition() - o.getPosition();
    }

    @Override
    public Integer getPosition() {
        return Optional.ofNullable(super.getPosition()).orElse(0);
    }

    public static void fixOrder(List<MenuItem> sortedItems) {
        for (int i = 0; i < sortedItems.size(); ++i) {

            sortedItems.get(i).setPosition(i);
        }
    }

    public static MenuItem create(Menu menu, Page page, LocalizedString name, MenuItem parent) {
        MenuItem menuItem = new MenuItem(menu);
        menuItem.setName(name);
        menuItem.setPage(page);
        menuItem.setFolder(page == null);
        if (menu != null) {
            if (parent != null) {
                parent.add(menuItem);
            } else {
                menu.add(menuItem);
            }
        }
        return menuItem;
    }

    public class MenuItemWrap extends Wrap {
        private final boolean active;
        private final boolean open;
        private final List<Wrap> children;

        public MenuItemWrap() {
            children =
                    MenuItem.this.getChildrenSorted().stream().filter(MenuItem::isVisible).map((menuItem) -> menuItem.makeWrap())
                            .collect(Collectors.toList());
            active = false;
            open = false;
        }

        public MenuItemWrap(Page page) {
            open = MenuItem.this.getPage() != null && MenuItem.this.getPage().equals(page);
            children =
                    ImmutableList.copyOf(MenuItem.this.getChildrenSorted().stream().filter(MenuItem::isVisible)
                            .map(menuItem -> menuItem.makeWrap(page)).collect(Collectors.toList()));
            active = open || children.stream().map(menuItem -> ((MenuItemWrap) menuItem).open).reduce(false, (x, y) -> x || y);
        }

        public List<Wrap> getChildren() {
            return children;
        }

        public LocalizedString getName() {
            return MenuItem.this.getName();
        }

        public String getAddress() {
            return MenuItem.this.getAddress();
        }

        public boolean isActive() {
            return active;
        }

        public boolean isOpen() {
            return open;
        }

        public boolean isFolder() {
            return MenuItem.this.getFolder();
        }
    }

    @Override
    public Wrap makeWrap() {
        return new MenuItemWrap();
    }

    public Wrap makeWrap(Page page) {
        return new MenuItemWrap(page);
    }

    public boolean isVisible() {
        return getPage() == null || getPage().isPublished();
    }
}
