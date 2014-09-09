package org.fenixedu.bennu.cms.domain;

import java.util.UUID;

import org.fenixedu.bennu.cms.domain.component.Component;
import org.fenixedu.bennu.cms.exceptions.CmsDomainException;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.AnyoneGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;

import com.google.common.base.Joiner;

/**
 * Model for a page on a given Site.
 */
public class Page extends Page_Base {
    private static final Logger log = LoggerFactory.getLogger(Page.class);

    /**
     * the logged {@link User} creates a new Page.
     */
    public Page() {
        super();
        DateTime now = new DateTime();
        this.setCreationDate(now);
        this.setModificationDate(now);
        if (Authenticate.getUser() == null) {
            throw CmsDomainException.forbiden();
        }
        this.setCreatedBy(Authenticate.getUser());
        this.setCanViewGroup(AnyoneGroup.get());
    }

    @Override
    public void setName(LocalizedString name) {
        LocalizedString prevName = getName();
        super.setName(name);

        this.setModificationDate(new DateTime());
        if (prevName == null) {
            setSlug(StringNormalizer.slugify(name.getContent()));
        }
    }

    @Override
    public void setSlug(String slug) {
        if (slug == null) {
            slug = "";
        }

        slug = StringNormalizer.slugify(slug);

        while (!isValidSlug(slug)) {
            String randomSlug = UUID.randomUUID().toString().substring(0, 3);
            slug = Joiner.on("-").join(slug, randomSlug);
        }

        super.setSlug(slug);

        if (slug == "" && getSite().getInitialPage() == null) {
            getSite().setInitialPage(this);
        }
    }

    /**
     * A slug is valid if there are no other page on that site that have the same slug.
     *
     * @param slug
     * @return true if it is a valid slug.
     */
    private boolean isValidSlug(String slug) {
        return getSite().pageForSlug(slug) == null;
    }

    /**
     * Searches a {@link Component} of this page by oid.
     *
     * @param oid
     *            the oid of the {@link Component} to be searched.
     * @return
     *         the {@link Component} with the given oid if it is a component of this page and null otherwise.
     */
    public Component componentForOid(String oid) {
        for (Component c : getComponentsSet()) {
            if (c.getExternalId().equals(oid)) {
                return c;
            }
        }
        return null;
    }

    @Atomic
    public void delete() {
        for (Component component : getComponentsSet()) {
            this.removeComponents(component);
            component.delete();
        }

        for (MenuItem mi : getMenuItemsSet()) {
            mi.delete();
        }

        this.setTemplate(null);
        this.setSite(null);
        this.setCreatedBy(null);
        this.setViewGroup(null);
        this.deleteDomainObject();
    }

    /**
     * @return the URL link for this page.
     */
    public String getAddress() {
        return CoreConfiguration.getConfiguration().applicationUrl() + "/" + getSite().getBaseUrl() + "/" + getSlug();
    }

    /**
     * returns the group of people who can view this site.
     *
     * @return group
     *         the access group for this site
     */
    public Group getCanViewGroup() {
        return getViewGroup().toGroup();
    }

    /**
     * sets the access group for this site
     *
     * @param group
     *            the group of people who can view this site
     */
    @Atomic
    public void setCanViewGroup(Group group) {
        setViewGroup(group.toPersistentGroup());
    }

    public static Page create(Site site, Menu menu, MenuItem parent, LocalizedString name, boolean published, String template,
            User creator, Component... components) {
        Page page = new Page();
        page.setSite(site);
        page.setName(name);
        if (components != null && components.length > 0) {
            for (Component component : components) {
                page.addComponents(component);
            }
        }
        page.setTemplate(site.getTheme().templateForType(template));
        if (creator == null) {
            page.setCreatedBy(site.getCreatedBy());
        } else {
            page.setCreatedBy(creator);
        }
        page.setPublished(published);
        if (menu != null) {
            MenuItem.create(menu, page, name, parent);
        }
        return page;
    }

    public static Page createBasePage(CMSTemplate template, Component... components) {
        Page page = new Page();
        page.setTemplate(template);
        if (components != null && components.length > 0) {
            for (Component component : components) {
                page.addComponents(component);
            }
        }
        return page;
    }

    @Override
    public void setPublished(Boolean published) {
        setModificationDate(new DateTime());
        super.setPublished(published);
    }

    @Override
    public void setTemplate(CMSTemplate template) {
        setModificationDate(new DateTime());
        super.setTemplate(template);
    }
}
