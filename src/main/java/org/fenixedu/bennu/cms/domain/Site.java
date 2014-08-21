package org.fenixedu.bennu.cms.domain;

import static java.util.stream.Collectors.toSet;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.fenixedu.bennu.cms.routing.CMSBackend;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.AnyoneGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class Site extends Site_Base {
    /**
     * maps the registered template types on the tempate classes
     */
    protected static final HashMap<String, Class<?>> TEMPLATES = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(Site.class);

    /**
     * registers a new site template
     * 
     * @param type
     *            the type of the template. This must be unique on the application.
     * @param c
     *            the class to be registered as a template.
     */
    public static void register(String type, Class<?> c) {
        TEMPLATES.put(type, c);
    }

    /**
     * searches for a {@link SiteTemplate} by type.
     * 
     * @param type
     *            the type of the {@link SiteTemplate} wanted.
     * @return
     *         the {@link SiteTemplate} with the given type if it exists or null otherwise.
     */
    public static SiteTemplate templateFor(String type) {
        try {
            return (SiteTemplate) TEMPLATES.get(type).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Error while instancing a site template", e);
            return null;
        }
    }

    /**
     * 
     * @return mapping between the type and description for all the registered {@link SiteTemplate}.
     */
    public static HashMap<String, String> getTemplates() {
        HashMap<String, String> map = new HashMap<>();

        for (Class<?> c : TEMPLATES.values()) {
            RegisterSiteTemplate registerSiteTemplate = c.getAnnotation(RegisterSiteTemplate.class);
            map.put(registerSiteTemplate.type(), registerSiteTemplate.name() + " - " + registerSiteTemplate.description());
        }

        return map;
    }

    /**
     * the logged {@link User} creates a new {@link Site}.
     */
    public Site() {
        super();
        if (Authenticate.getUser() == null) {
            throw new RuntimeException("Needs Login");
        }
        this.setCreatedBy(Authenticate.getUser());
        this.setCreationDate(new DateTime());
        this.setCanViewGroup(AnyoneGroup.get());
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

    /**
     * searches for a {@link Site} by slug.
     * 
     * @param slug
     *            the slug of the {@link Site} wanted.
     * @return
     *         the {@link Site} with the given slug if it exists, or null otherwise.
     */
    public static Site fromSlug(String slug) {
        return Bennu.getInstance().getSitesSet().stream().filter(site -> slug.equals(site.getSlug())).findAny().orElse(null);
    }

    /**
     * searches for a {@link Page} by slug on this {@link Site}.
     * 
     * @param slug
     *            the slug of the {@link Page} wanted.
     * @return
     *         the {@link Page} with the given slug if it exists on this site, or null otherwise.
     */
    public Page pageForSlug(String slug) {
        if ((Strings.isNullOrEmpty(slug) || slug.startsWith("/")) && getInitialPage() != null) {
            return getInitialPage();
        } else {
            return getPagesSet().stream().filter(page -> slug.equals(page.getSlug())).findAny().orElse(null);
        }
    }

    /**
     * searches for a {@link Post} by slug on this {@link Site}.
     * 
     * @param slug
     *            the slug of the {@link Post} wanted.
     * @return
     *         the {@link Post} with the given slug if it exists on this site, or null otherwise.
     */
    public Post postForSlug(String slug) {
        return getPostSet().stream().filter(post -> post.getSlug().equals(slug)).findAny().orElse(null);
    }

    /**
     * searches for a {@link Category} by slug on this {@link Site}.
     * 
     * @param slug
     *            the slug of the {@link Category} wanted.
     * @return
     *         the {@link Category} with the given slug if it exists on this site, or null otherwise.
     */
    public Category categoryForSlug(String slug) {
        return getCategoriesSet().stream().filter(category -> category.getSlug().equals(slug)).findAny().orElse(null);
    }

    public Category categoryForSlug(String slug, LocalizedString name) {
        Category c = categoryForSlug(slug);
        if (c == null) {
            c = new Category();
            c.setName(name);
            c.setSlug(slug);
            c.setSite(this);
        }
        return c;
    }

    /**
     * searches for a {@link Menu} by oid on this {@link Site}.
     * 
     * @param oid
     *            the slug of the {@link Menu} wanted.
     * @return
     *         the {@link Menu} with the given oid if it exists on this site, or null otherwise.
     */
    public Menu menuForOid(String oid) {
        Menu menu = FenixFramework.getDomainObject(oid);
        if (menu == null || menu.getSite() != this) {
            return null;
        } else {
            return menu;
        }
    }

    @Atomic
    private void deleteMenuFunctionality() {
        MenuFunctionality mf = this.getFunctionality();
        this.setFunctionality(null);
        mf.delete();
    }

    /**
     * Updates the site's slug and it's respective MenuFunctionality.
     * It should be used after setting the site's description and name.
     * 
     * @param slug
     *            the slug wanted. It must be the only site with this slug or else a random slug is generated.
     */
    @Override
    public void setSlug(String slug) {
        Preconditions.checkNotNull(this.getDescription());
        Preconditions.checkNotNull(this.getName());

        while (!isValidSlug(slug)) {
            String randomSlug = UUID.randomUUID().toString().substring(0, 3);
            slug = Joiner.on("-").join(slug, randomSlug);
        }

        super.setSlug(slug);

        if (this.getFunctionality() != null) {
            deleteMenuFunctionality();
        }

        this.setFunctionality(new MenuFunctionality(PortalConfiguration.getInstance().getMenu(), false, slug,
                CMSBackend.BACKEND_KEY, "anyone", this.getDescription(), this.getName(), slug));
    }

    @Atomic
    public void delete() {
        MenuFunctionality mf = this.getFunctionality();
        this.setFunctionality(null);

        if (mf != null) {
            mf.delete();
        }

        for (Post post : getPostSet()) {
            post.delete();
        }

        for (Category cat : getCategoriesSet()) {
            cat.delete();
        }

        for (Menu cat : getMenusSet()) {
            cat.delete();
        }

        for (Page page : getPagesSet()) {
            page.delete();
        }
        this.setViewGroup(null);
        this.setTheme(null);
        this.setCreatedBy(null);
        this.setBennu(null);
        this.deleteDomainObject();
    }

    /**
     * @return the {@link ViewPost} of this {@link Site} if it is defined, or null otherwise.
     */
    public Page getViewPostPage() {
        for (Page page : getPagesSet()) {
            for (Component component : page.getComponentsSet()) {
                if (component.getClass() == ViewPost.class) {
                    return page;
                }
            }
        }
        return null;
    }

    /**
     * @return the {@link ListCategoryPosts} of this {@link Site} if it is defined, or null otherwise.
     */
    public Page getViewCategoryPage() {
        for (Page page : getPagesSet()) {
            for (Component component : page.getComponentsSet()) {
                if (component.getClass() == ListCategoryPosts.class) {
                    return page;
                }
            }
        }
        return null;
    }

    /**
     * @return the static directory of this {@link Site}.
     */
    public String getStaticDirectory() {
        String path = CoreConfiguration.getConfiguration().applicationUrl();
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        return path + this.getSlug() + "/static";
    }

    /**
     * @return the object associated with this {@link Site}.
     */
    public DomainObject getObject() {
        return null;
    }

    public static boolean isValidSlug(String slug) {
        Stream<MenuItem> menuItems = Bennu.getInstance().getConfiguration().getMenu().getOrderedChild().stream();
        Optional<String> existsEntry = menuItems.map(i -> i.getPath()).filter(path -> path.equals(slug)).findFirst();
        return !Strings.isNullOrEmpty(slug) && fromSlug(slug) == null && !existsEntry.isPresent();
    }

    @Override
    public Page getInitialPage() {
        return Optional.ofNullable(super.getInitialPage()).orElseGet(() -> getPagesSet().stream().findFirst().orElse(null));
    }

    public Set<Menu> getSideMenus() {
        return getMenusSet().stream().filter(m -> !m.getComponentsOfClass(SideMenuComponent.class).isEmpty()).collect(toSet());
    }

    public Set<Menu> getTopMenus() {
        return getMenusSet().stream().filter(m -> !m.getComponentsOfClass(TopMenuComponent.class).isEmpty()).collect(toSet());
    }

}
