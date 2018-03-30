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
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.signals.DomainObjectEvent;
import org.fenixedu.bennu.core.signals.Signal;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.component.ListCategoryPosts;
import org.fenixedu.cms.domain.component.StaticPost;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.fenixframework.Atomic;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.fenixedu.commons.i18n.LocalizedString.fromJson;

/**
 * Model for a page on a given Site.
 */
public class Page extends Page_Base implements Sluggable, Cloneable {

    public static final String SIGNAL_CREATED = "fenixedu.cms.page.created";
    public static final String SIGNAL_DELETED = "fenixedu.cms.page.deleted";
    public static final String SIGNAL_EDITED = "fenixedu.cms.page.edited";
    public static final String SIGNAL_ARCHIVED = "fenixedu.cms.page.archived";
    public static final String SIGNAL_RECOVERED = "fenixedu.cms.page.recovered";

    public static final Comparator<Page> CREATION_DATE_COMPARATOR = Comparator.comparing(Page::getCreationDate).reversed();
    public static Comparator<Page> PAGE_NAME_COMPARATOR = Comparator.comparing(Page::getName);
    
    private static final Logger logger = LoggerFactory.getLogger(Page.class);
    
    /**
     * the logged {@link User} creates a new Page.
     * @param site site
     * @param name name
     */
    public Page(Site site, LocalizedString name) {
        super();
        DateTime now = new DateTime();
        setCreationDate(now);
        setModificationDate(now);
        if (Authenticate.getUser() == null) {
            throw CmsDomainException.forbiden();
        }
        setCreatedBy(Authenticate.getUser());
        setCanViewGroup(Group.anyone());
        setSite(site);
        setPublished(false);
        setName(name);

        Signal.emit(Page.SIGNAL_CREATED, new DomainObjectEvent<Page>(this));
    }

    @Override
    public Site getSite() {
        return super.getSite();
    }

    @Override
    public void setName(LocalizedString name) {
        LocalizedString prevName = getName();
        super.setName(name);

        setModificationDate(new DateTime());
        if (prevName == null) {
            setSlug(StringNormalizer.slugify(name.getContent()));
        }
    }

    @Override
    public void setSlug(String slug) {
        super.setSlug(SlugUtils.makeSlug(this, slug));
    }

    /**
     * A slug is valid if there are no other page on that site that have the
     * same slug.
     *
     * @param slug slug
     * @return true if it is a valid slug.
     */
    @Override
    public boolean isValidSlug(String slug) {
        try {
            return this == getSite().getArchivedPagesSet().stream()
                    .filter(page -> page.getSlug() != null)
                    .filter(page -> page.getSlug().equals(slug))
                    .findAny().orElse(getSite().pageForSlug(slug));
        } catch (CmsDomainException cmsDomainException){
            return true;
        }
    }

    /**
     * Searches a {@link Component} of this page by oid.
     *
     * @param oid
     *            the oid of the {@link Component} to be searched.
     * @return the {@link Component} with the given oid if it is a component of
     *         this page and null otherwise.
     */
    public Component componentForOid(String oid) {
        for (Component c : getComponentsSet()) {
            if (c.getExternalId().equals(oid)) {
                return c;
            }
        }
        return null;
    }
    
    @Override
    public void removeComponents(Component components) {
        logger.info("Page " + getSlug() + " - " + getExternalId() + " of Site " + getSite().getSlug() +
                " component " + components.getType() + " removed by user "+ Authenticate.getUser().getUsername());
        super.removeComponents(components);
    }
    
    @Override
    public void addComponents(Component components) {
        logger.info("Page " + getSlug() + " - " + getExternalId() + " of Site " + getSite().getSlug() +
                " component " + components.getType() + " added by user "+ Authenticate.getUser().getUsername());
        super.addComponents(components);
    }
    
    @Atomic
    public void delete() {
        logger.info("Page " + getSlug() + " - " + getExternalId() + " of Site " + getSite().getSlug() +
                " deleted by user "+ Authenticate.getUser().getUsername());
        Signal.emit(SIGNAL_DELETED, this.getOid());

        for (Component component : getComponentsSet()) {
            removeComponents(component);
            component.delete();
        }

        getMenuItemsSet().stream().forEach(MenuItem::delete);

        setTemplate(null);
        setSite(null);
        setCreatedBy(null);
        setViewGroup(null);
        deleteDomainObject();
    }

    @Atomic
    public void archive() {
        setPublished(false);
        getStaticPost().ifPresent(post -> post.setActive(false));
        setArchivedSite(getSite());
        setSite(null);
        getMenuItemsSet().stream().forEach(MenuItem::delete);

        Signal.emit(SIGNAL_ARCHIVED, this.getOid());
    }

    @Atomic
    public void recover() {
        setSite(getArchivedSite());
        setArchivedSite(null);

        Signal.emit(SIGNAL_RECOVERED, this.getOid());
    }

    /**
     * @return the URL link for this page.
     */
    public String getAddress() {
        if (getSite() == null) {
            return null;
        }
        return CoreConfiguration.getConfiguration().applicationUrl() + "/" + getSite().getBaseUrl() + "/" + getSlug();
    }

    /**
     * returns the group of people who can view this site.
     *
     * @return group the access group for this site
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
        Page page = new Page(site, name);
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

    @Override
    public void setPublished(boolean published) {
        setModificationDate(new DateTime());
        super.setPublished(published);
    }

    @Override
    public void setTemplate(CMSTemplate template) {
        setModificationDate(new DateTime());
        super.setTemplate(template);
        if (template != null) {
            setTemplateType(template.getType());
        } else {
            setTemplateType(null);
        }
    
        logger.info("Page " + getSlug() + " of Site " + getSite().getSlug() +
                " template changed by user "+ Authenticate.getUser());
    }

    public boolean isPublished() {
        return getPublished();
    }

    public String getRssUrl() {
        for (Component component : getComponentsSet()) {
            if (component instanceof ListCategoryPosts) {
                ListCategoryPosts listPosts = (ListCategoryPosts) component;
                if (listPosts.getCategory() != null) {
                    return listPosts.getCategory().getRssUrl();
                }
            }
        }
        return null;
    }

    public String getEditUrl() {
        if (isStaticPage()) {
            return CoreConfiguration.getConfiguration().applicationUrl() + "/cms/pages/" + getSite().getSlug() + "/" + getSlug()
                    + "/edit";
        } else {
            return CoreConfiguration.getConfiguration().applicationUrl() + "/cms/pages/advanced/" + getSite().getSlug() + "/"
                    + getSlug() + "/edit";
        }
    }

    @Override
    public CMSTemplate getTemplate() {
        String templateType = getTemplateType();
        if (templateType == null) {
            return null;
        }
        CMSTemplate template = super.getTemplate();
        CMSTheme theme = getSite().getTheme();
        if (templateType != null) {
            if (template != null && template.getTheme() == theme && template.getType().equals(templateType)) {
                return template;
            }

            if (theme != null) {
                template = theme.templateForType(templateType);
                if (template != null) {
                    return template;
                }
            }

            if (theme.getDefaultTemplate() != null) {
                return theme.getDefaultTemplate();
            }

            return CMSTheme.getDefaultTheme().getDefaultTemplate();

        } else {
            return null;
        }
    }

    public boolean isStaticPage() {
        return getComponentsSet().stream().filter(component -> StaticPost.class.isInstance(component)).findAny().isPresent();
    }

    public Optional<Post> getStaticPost() {
        return getComponentsSet().stream().filter(component -> StaticPost.class.isInstance(component))
                .map(component -> ((StaticPost) component).getPost()).findFirst();
    }

    @Override
    public Page clone(CloneCache cloneCache) {
        return cloneCache.getOrClone(this, obj -> {
            Set<Component> components = new HashSet<>(getComponentsSet());
            LocalizedString name = getName() != null ? fromJson(getName().json()) : null;
            Page clone = new Page(getSite(), name);
            cloneCache.setClone(Page.this, clone);
            clone.setCanViewGroup(getCanViewGroup());
            clone.setPublished(getPublished());
            clone.setCreatedBy(getCreatedBy());
            clone.setModificationDate(getModificationDate());
            clone.setCreationDate(getCreationDate());
            clone.setTemplateType(getTemplateType());
            clone.setViewGroup(getViewGroup());
            components.stream().map(c -> c.clone(cloneCache)).forEach(clone::addComponents);
            return clone;
        });
    }
}
