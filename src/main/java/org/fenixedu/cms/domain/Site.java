package org.fenixedu.cms.domain;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.cms.routing.CMSBackend;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

public class Site extends Site_Base {

    protected static final HashMap<String, Class<?>> TEMPLATES = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(Site.class);

    protected static void register(String type, Class c) {
        TEMPLATES.put(type, c);
    }

    public static SiteTemplate templateFor(String type) {
        try {
            return (SiteTemplate) TEMPLATES.get(type).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Error while instancing a site template", e);
            return null;
        }
    }

    public static HashMap<String, String> getTemplates() {
        HashMap<String, String> map = new HashMap<>();

        for (Class c : TEMPLATES.values()) {
            RegisterSiteTemplate registerSiteTemplate = (RegisterSiteTemplate) c.getAnnotation(RegisterSiteTemplate.class);
            map.put(registerSiteTemplate.type(), registerSiteTemplate.name() + " - " + registerSiteTemplate.description());
        }

        return map;
    }

    public Site() {
        super();
        if (Authenticate.getUser() == null) {
            throw new RuntimeException("Needs Login");
        }
        this.setCreatedBy(Authenticate.getUser());
        this.setCreationDate(new DateTime());

    }

    public static Site fromSlug(String slug) {
        for (Site site : Bennu.getInstance().getSitesSet()) {
            if (site.getSlug().equals(slug)) {
                return site;
            }
        }
        return null;
    }

    public Page pageForSlug(String slug) {
        for (Page page : getPagesSet()) {
            if (page.getSlug().equals(slug)) {
                return page;
            }
        }
        return null;
    }

    public Post postForSlug(String slug) {
        for (Post post : getPostSet()) {
            if (post.getSlug().equals(slug)) {
                return post;
            }
        }
        return null;
    }

    public Category categoryForSlug(String slug) {
        for (Category category : getCategoriesSet()) {
            if (category.getSlug().equals(slug)) {
                return category;
            }
        }
        return null;
    }

    public Menu menuForOid(String oid) {
        Menu menu = FenixFramework.getDomainObject(oid);
        if (menu == null || menu.getSite() != this) {
            return null;
        } else {
            return menu;
        }
    }

    // To Remove
    @Deprecated
    public static String slugify(String name) {
        Pattern NONLATIN = Pattern.compile("[^\\w-]");
        Pattern WHITESPACE = Pattern.compile("[\\s]");
        name = name.trim();
        name = Normalizer.normalize(name, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String nowhitespace = WHITESPACE.matcher(name).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        name = slug.toLowerCase(Locale.ENGLISH);
        return name;
    }

    @Override
    public void setName(LocalizedString name) {
        LocalizedString prevName = getName();
        super.setName(name);

        if (prevName == null) {
            setSlug(slugify(name.getContent()));
        }
    }

    @Override
    // TODO: either prevent setting duplicated slugs or attach postfix.
    public void setSlug(String slug) {
        super.setSlug(slug);
        if (this.getFunctionality() != null) {
            this.getFunctionality().delete();
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

        this.setTheme(null);
        this.setCreatedBy(null);
        this.setBennu(null);
        this.deleteDomainObject();
    }

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
    
    public String getStaticDirectory() {
        String path = CoreConfiguration.getConfiguration().applicationUrl();
        if (!path.endsWith("/")) {
            path += path + "/";
        }
        return path + this.getSlug() + "/static";
    }
}
