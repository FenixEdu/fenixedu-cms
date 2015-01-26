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
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.wraps.Wrap;
import org.fenixedu.cms.domain.wraps.Wrappable;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

import com.google.common.collect.Sets;

/**
 * Categories give a semantic group for {@link Site} and {@link Post}.
 */
public class Category extends Category_Base implements Wrappable, Sluggable {
    /**
     * The logged {@link User} creates a new instance of a {@link Category}
     */
    public Category(Site site) {
        super();
        if (Authenticate.getUser() == null) {
            throw CmsDomainException.forbiden();
        }
        this.setCreatedBy(Authenticate.getUser());
        this.setCreationDate(new DateTime());
        this.setSite(site);
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
     * @param slug
     * @return true if it is a valid slug.
     */
    @Override
    public boolean isValidSlug(String slug) {
        Category c = getSite().categoryForSlug(slug);
        return c == null || c == this;
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
        for (Component c : this.getComponentsSet()) {
            c.delete();
        }
        this.setCreatedBy(null);
        this.setSite(null);
        for (Post post : this.getPostsSet()) {
            post.removeCategories(this);
        }
        this.deleteDomainObject();
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
}
