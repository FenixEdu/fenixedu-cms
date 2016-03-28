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

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.signals.DomainObjectEvent;
import org.fenixedu.bennu.core.signals.Signal;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.component.ListCategoryPosts;
import org.fenixedu.cms.domain.wraps.Wrap;
import org.fenixedu.cms.domain.wraps.Wrappable;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import pt.ist.fenixframework.Atomic;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.fenixedu.commons.i18n.LocalizedString.fromJson;

/**
 * Categories give a semantic group for {@link Site} and {@link Post}.
 */
public class Category extends Category_Base implements Wrappable, Sluggable, Cloneable {
    private static final long NUM_RECENT = 10;
    public static final String SIGNAL_CREATED = "fenixedu.cms.category.created";
    public static final String SIGNAL_DELETED = "fenixedu.cms.category.deleted";
    public static final String SIGNAL_EDITED = "fenixedu.cms.category.edited";

    public static final Comparator<? super Category> CATEGORY_NAME_COMPARATOR = Comparator.comparing(Category::getName);

    /**
     * The logged {@link User} creates a new instance of a {@link Category}
     * @param site site
     * @param name name
     */
    public Category(Site site, LocalizedString name) {
        super();
        if (Authenticate.getUser() == null) {
            throw CmsDomainException.forbiden();
        }
        this.setCreatedBy(Authenticate.getUser());
        this.setCreationDate(new DateTime());
        this.setSite(site);
        this.setName(name);
        this.setPrivileged(false);
        Signal.emit(Category.SIGNAL_CREATED, new DomainObjectEvent<Category>(this));
    }

    @Override
    public Site getSite() {
        return super.getSite();
    }

    @Override
    public void setName(LocalizedString name) {
        LocalizedString prevName = getName();
        super.setName(name);

        if (prevName == null) {
            setSlug(StringNormalizer.slugify(name.getContent()));
        }
    }

    @Override
    public void setSlug(String slug) {
        super.setSlug(SlugUtils.makeSlug(this, slug));
    }

    /**
     * A slug is valid if there are no other category on that site that have the same slug.
     *
     * @param slug slug
     * @return true if it is a valid slug.
     */
    @Override
    public boolean isValidSlug(String slug) {
        try {
            Category c = getSite().categoryForSlug(slug);
            return c == this;
        } catch (CmsDomainException cmsDomainException){
            return true;
        }
    }

    public String getAddress() {
        Page viewCategoryPage = this.getSite().getViewCategoryPage();
        return viewCategoryPage == null ? null : viewCategoryPage.getAddress() + "/" + this.getSlug();
    }

    public String getRssUrl() {
        return getSite().getRssUrl() + "/" + getSlug();
    }

    @Atomic
    public void delete() {
        Signal.emit(SIGNAL_DELETED, this.getOid());
        this.setCreatedBy(null);
        this.setSite(null);
        this.getComponentsSet().stream().forEach(Component::delete);
        this.getPostsSet().stream().forEach(post->post.removeCategories(this));
        this.deleteDomainObject();
    }

    @Override
    public Category clone(CloneCache cloneCache) {
        return cloneCache.getOrClone(this, obj -> {
            Collection<Post> posts = new HashSet<>(getPostsSet());
            HashSet<Component> components = new HashSet<>(getComponentsSet());
            LocalizedString name = getName() != null ? fromJson(getName().json()) : null;
            Category clone = new Category(getSite(), name);
            cloneCache.setClone(Category.this, clone);
            posts.stream().map(post -> post.clone(cloneCache)).forEach(clone::addPosts);
            components.stream().filter(ListCategoryPosts.class::isInstance)
                    .map(ListCategoryPosts.class::cast)
                    .forEach(clone::addComponents);
            return clone;
        });
    }

    public class CategoryWrap extends Wrap {

        public LocalizedString getName() {
            return Category.this.getName();
        }

        public String getAddress() {
            return Category.this.getAddress();
        }

        public String getSlug() {
            return Category.this.getSlug();
        }

        public DateTime getCreationDate() {
            return Category.this.getCreationDate();
        }

        public String getRssUrl() {
            return Category.this.getRssUrl();
        }

    }

    @Override
    public Wrap makeWrap() {
        return new CategoryWrap();
    }

    public String getEditUrl() {
        return CoreConfiguration.getConfiguration().applicationUrl() + "/cms/categories/" + getSite().getSlug() + "/" + getSlug();
    }

    public List<Post> getLatestPosts() {
        return getPostsSet().stream().sorted(Post.CREATION_DATE_COMPARATOR).collect(toList());
    }
}
