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

import static org.fenixedu.commons.i18n.LocalizedString.fromJson;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.signals.DomainObjectEvent;
import org.fenixedu.bennu.signals.Signal;
import org.fenixedu.cms.domain.wraps.Wrap;
import org.fenixedu.cms.domain.wraps.Wrappable;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.consistencyPredicates.ConsistencyPredicate;

/**
 * Model of a Menu for a given {@link Page}
 */
public class Menu extends Menu_Base implements Wrappable, Sluggable, Cloneable, Comparable<Menu>{

    public static final String SIGNAL_CREATED = "fenixedu.cms.menu.created";
    public static final String SIGNAL_DELETED = "fenixedu.cms.menu.deleted";
    public static final String SIGNAL_EDITED = "fenixedu.cms.menu.edited";


    public Menu(Site site, LocalizedString name) {
        if (Authenticate.getUser() == null) {
            throw CmsDomainException.forbiden();
        }
        this.setCreatedBy(Authenticate.getUser());
        this.setCreationDate(new DateTime());

        setSite(site);
        setTopMenu(false);

        this.setName(name);
        this.setPrivileged(false);

        this.setOrder(site.getMenusSet().size());

        Signal.emit(Menu.SIGNAL_CREATED, new DomainObjectEvent<>(this));
    }

    @Override
    public Site getSite() {
        return super.getSite();
    }

    @Atomic
    public void delete() {
        Signal.emit(Menu.SIGNAL_DELETED, new DomainObjectEvent<>(this));
        Sets.newHashSet(getItemsSet()).stream().distinct().forEach(MenuItem::delete);
        this.setCreatedBy(null);
        this.setSite(null);
        this.deleteDomainObject();
    }

    @Override
    public void setSlug(String slug) {
        super.setSlug(SlugUtils.makeSlug(this, slug));
    }

    /**
     * A slug is valid if there are no other page on that site that have the same slug.
     *
     * @param slug
     * @return true if it is a valid slug.
     */
    @Override
    public boolean isValidSlug(String slug) {
        Menu m = getSite().menuForSlug(slug);
        return m == null || m == this;
    }

    /**
     * saves the name of the post and creates a new slug for the post.
     */
    @Override
    public void setName(LocalizedString name) {
        LocalizedString prevName = getName();
        super.setName(name);
        if (prevName == null) {
            String slug = StringNormalizer.slugify(name.getContent());
            setSlug(slug);
        }
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

    @Override
    public Menu clone(CloneCache cloneCache) {
        return cloneCache.getOrClone(this, obj -> {
            Collection<MenuItem> menuItems = new HashSet<>(getItemsSet());
            LocalizedString name = getName() != null ? fromJson(getName().json()) : null;

            Menu clone = new Menu(getSite(), name);
            cloneCache.setClone(Menu.this, clone);
            clone.setName(name);
            clone.setOrder(getOrder());
            for (MenuItem menuItem : menuItems) {
                menuItem.clone(cloneCache).setMenu(clone);
            }

            return clone;
        });
    }

    @Override
    public int compareTo(Menu o) {
        return getOrder().compareTo(o.getOrder());
    }

    @SuppressWarnings("unused")
    private class MenuWrap extends Wrap {
        private final Page page;
        private final Stream<Wrap> children;

        public MenuWrap() {
            this.page = null;
            this.children = getToplevelItemsSorted().filter(MenuItem::isVisible).map(MenuItem::makeWrap);
        }

        public MenuWrap(Page page) {
            this.page = page;
            this.children = getToplevelItemsSorted().filter(MenuItem::isVisible).map(item -> item.makeWrap(page));
        }

        public Stream<Wrap> getChildren() {
            return children;
        }

        public int getOrder(){
            return Menu.this.getOrder();
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

    public MenuItem menuItemForOid(String menuItemOid) {
        MenuItem menuItem = FenixFramework.getDomainObject(menuItemOid);
        if(menuItem != null && FenixFramework.isDomainObjectValid(menuItem) && menuItem.getMenu() == this) {
            return menuItem;
        }
        return null;
    }

    @ConsistencyPredicate
    public boolean checkMenuOrder(){
        return getOrder() != null && !(getOrder()<0);
    }
}
