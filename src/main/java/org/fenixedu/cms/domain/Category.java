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
public class Category extends Category_Base implements Wrappable {
    /**
     * The logged {@link User} creates a new instance of a {@link Category}
     */
    public Category() {
        super();
        if (Authenticate.getUser() == null) {
            throw CmsDomainException.forbiden();
        }
        this.setCreatedBy(Authenticate.getUser());
        this.setCreationDate(new DateTime());
    }

    @Override
    public void setName(LocalizedString name) {
        LocalizedString prevName = getName();
        super.setName(name);

        if (prevName == null) {
            setSlug(StringNormalizer.slugify(name.getContent()));
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
        for (Component c : this.getComponentsSet()) {
            c.delete();
        }
        this.setCreatedBy(null);
        this.setSite(null);
        for (Post post : this.getPostsSet()) {
            for (Category c : Sets.newHashSet(post.getCategoriesSet())) {
                post.removeCategories(c);
            }
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

    }

    @Override
    public Wrap makeWrap() {
        return new CategoryWrap();
    }
}
