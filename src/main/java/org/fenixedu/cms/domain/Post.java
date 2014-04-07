package org.fenixedu.cms.domain;

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

    /**
     * The logged {@link User} creates a new Post.
     */
    public Post() {
        super();
        if (Authenticate.getUser() == null) {
            throw new RuntimeException("Needs Login");
        }
        this.setCreatedBy(Authenticate.getUser());
        this.setCreationDate(new DateTime());
    }

    /**
     * saves the name of the post and creates a new slug for the post.
     */
    @Override
    public void setName(LocalizedString name) {
        LocalizedString prevName = getName();
        super.setName(name);

        if (prevName == null) {
            setSlug(Site.slugify(name.getContent()));
        }
    }
    
    /**
     * @return the URL link to the slug's page.
     */
    public String getAddress() {
        String path = CoreConfiguration.getConfiguration().applicationUrl();
        if (path.charAt(path.length()-1) != '/') {
            path += "/";
        }
        Page page = this.getSite().getViewPostPage();
        path += this.getSite().getSlug() + "/" + page.getSlug() + "?q=" + this.getSlug();
        return path;
    }
    
    @Atomic
    public void delete() {
        for(Component c : this.getComponentSet()){
            c.delete();
        }
        this.setCreatedBy(null);
        this.setSite(null);
        this.setCategory(null);
        this.deleteDomainObject();
    }
}
