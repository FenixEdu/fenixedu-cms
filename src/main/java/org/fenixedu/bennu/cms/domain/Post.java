package org.fenixedu.bennu.cms.domain;

import java.util.Comparator;
import java.util.HashSet;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

/**
 * A post models a given content to be presented to the user.
 */
public class Post extends Post_Base {

    public static final Comparator<? super Post> CREATION_DATE_COMPARATOR = (o1, o2) -> o2.getCreationDate().compareTo(
            o1.getCreationDate());

    /**
     * The logged {@link User} creates a new Post.
     */
    public Post() {
        super();
        if (Authenticate.getUser() == null) {
            throw new RuntimeException("Needs Login");
        }
        this.setCreatedBy(Authenticate.getUser());
        DateTime now = new DateTime();
        this.setCreationDate(now);
        this.setModificationDate(now);
        this.setActive(true);
    }

    /**
     * saves the name of the post and creates a new slug for the post.
     */
    @Override
    public void setName(LocalizedString name) {
        LocalizedString prevName = getName();
        super.setName(name);
        this.setModificationDate(new DateTime());
        if (prevName == null) {
            setSlug(Site.slugify(name.getContent()));
        }
    }

    /**
     * @return the URL link to the slug's page.
     */
    public String getAddress() {
        Page page = this.getSite().getViewPostPage();;
        if (page == null && !this.getComponentSet().isEmpty()) {
            page = this.getComponentSet().iterator().next().getPage();
        }
        if (page != null) {
            String path = CoreConfiguration.getConfiguration().applicationUrl();
            if (path.charAt(path.length() - 1) != '/') {
                path += "/";
            }
            path += this.getSite().getSlug() + "/" + page.getSlug() + "?q=" + this.getSlug();
            return path;
        }
        return null;
    }

    @Atomic
    public void delete() {
        for (Component c : this.getComponentSet()) {
            c.delete();
        }
        for (Category c : this.getCategoriesSet()) {
            removeCategories(c);
        }

        this.setCreatedBy(null);
        this.setSite(null);
        this.deleteDomainObject();
    }

    public void removeCategories() {
        DateTime now = new DateTime();
        new HashSet<>(getCategoriesSet()).forEach(c -> {
            removeCategories(c);
            c.getComponentsSet().forEach(component -> {
                if (component.getPage() != null) {
                    component.getPage().setModificationDate(now);
                }
            });
        });
        setModificationDate(now);
    }

    public boolean hasPublicationPeriod() {
        return getPublicationBegin() != null && getPublicationEnd() != null;
    }

    public boolean isVisible() {
        boolean inPublicationPeriod =
                !hasPublicationPeriod() || (getPublicationBegin().isAfterNow() && getPublicationEnd().isBeforeNow());
        return getActive() && inPublicationPeriod;
    }

    public static Post create(Site site, Page page, LocalizedString name, LocalizedString body, Category category,
            boolean active, User creator) {
        Post post = new Post();
        post.setSite(site);
        post.setName(name);
        post.setBody(body);
        post.setCreationDate(new DateTime());
        if (creator == null) {
            post.setCreatedBy(page.getCreatedBy());
        } else {
            post.setCreatedBy(creator);
        }
        post.addCategories(category);
        post.setActive(active);
        return post;
    }

    @Override
    public void addCategories(Category categories) {
        DateTime now = new DateTime();
        super.addCategories(categories);
        categories.getComponentsSet().forEach(component -> {
            if (component.getPage() != null) {
                component.getPage().setModificationDate(now);
            }
        });
        setModificationDate(now);
    }

    @Override
    public void setActive(Boolean active) {
        super.setActive(active);
        setModificationDate(new DateTime());
    }

    @Override
    public void setBody(LocalizedString body) {
        super.setBody(body);
        setModificationDate(new DateTime());
    }

    @Override
    public void setLocation(LocalizedString location) {
        super.setLocation(location);
        setModificationDate(new DateTime());
    }

    @Override
    public void setPublicationEnd(DateTime publicationEnd) {
        super.setPublicationEnd(publicationEnd);
        setModificationDate(new DateTime());
    }

    @Override
    public void setPublicationBegin(DateTime publicationBegin) {
        super.setPublicationBegin(publicationBegin);
        setModificationDate(new DateTime());
    }

}
