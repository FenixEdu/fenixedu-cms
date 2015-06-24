package org.fenixedu.cms.api;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;

import org.fenixedu.bennu.core.groups.AnonymousGroup;
import org.fenixedu.bennu.core.groups.AnyoneGroup;
import org.fenixedu.bennu.core.groups.CustomGroupRegistry;
import org.fenixedu.bennu.core.groups.CustomGroupRegistry.BooleanParser;
import org.fenixedu.bennu.core.groups.CustomGroupRegistry.DateTimeParser;
import org.fenixedu.bennu.core.groups.CustomGroupRegistry.StringParser;
import org.fenixedu.bennu.core.groups.LoggedGroup;
import org.fenixedu.bennu.core.groups.NobodyGroup;
import org.fenixedu.bennu.core.groups.UserGroup;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.rest.DomainExceptionMapper;
import org.fenixedu.bennu.core.rest.DomainObjectParamConverter;
import org.fenixedu.bennu.core.rest.JsonAwareResource;
import org.fenixedu.cms.api.json.CategoryAdapter;
import org.fenixedu.cms.api.json.MenuAdapter;
import org.fenixedu.cms.api.json.MenuItemAdapter;
import org.fenixedu.cms.api.json.PageAdapter;
import org.fenixedu.cms.api.json.PostAdapter;
import org.fenixedu.cms.api.json.PostRevisionAdapter;
import org.fenixedu.cms.api.json.SiteAdapter;
import org.fenixedu.cms.api.json.ThemeAdapter;
import org.fenixedu.cms.api.resource.CategoryResource;
import org.fenixedu.cms.api.resource.MenuItemResource;
import org.fenixedu.cms.api.resource.MenuResource;
import org.fenixedu.cms.api.resource.PageResource;
import org.fenixedu.cms.api.resource.PostResource;
import org.fenixedu.cms.api.resource.RevisionResource;
import org.fenixedu.cms.api.resource.SiteResource;
import org.fenixedu.cms.api.resource.ThemeResource;
import org.fenixedu.cms.domain.CMSTheme;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.MenuItem;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.PostContentRevision;
import org.fenixedu.cms.domain.Site;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class TestCmsApi extends JerseyTest {

    protected static final String EMPTY_RESPONSE = "[]";

    protected JsonBuilder ctx = new JsonBuilder();

    @Override
    protected void configureClient(ClientConfig config) {
        super.configureClient(config);
        config.register(DomainObjectParamConverter.class);
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(DomainObjectParamConverter.class, SiteResource.class, PostResource.class,
                RevisionResource.class, PageResource.class, CategoryResource.class, MenuResource.class, MenuItemResource.class,
                ThemeResource.class, DomainExceptionMapper.class);
    }

    public static void ensure() {
        CustomGroupRegistry.registerCustomGroup(AnonymousGroup.class);
        CustomGroupRegistry.registerCustomGroup(AnyoneGroup.class);
        CustomGroupRegistry.registerCustomGroup(LoggedGroup.class);
        CustomGroupRegistry.registerCustomGroup(NobodyGroup.class);
        CustomGroupRegistry.registerCustomGroup(UserGroup.class);
        CustomGroupRegistry.registerArgumentParser(UserGroup.UserArgumentParser.class);
        CustomGroupRegistry.registerArgumentParser(BooleanParser.class);
        CustomGroupRegistry.registerArgumentParser(StringParser.class);
        CustomGroupRegistry.registerArgumentParser(DateTimeParser.class);

        JsonAwareResource.setDefault(Site.class, SiteAdapter.class);
        JsonAwareResource.setDefault(Post.class, PostAdapter.class);
        JsonAwareResource.setDefault(PostContentRevision.class, PostRevisionAdapter.class);
        JsonAwareResource.setDefault(Page.class, PageAdapter.class);
        JsonAwareResource.setDefault(Category.class, CategoryAdapter.class);
        JsonAwareResource.setDefault(Menu.class, MenuAdapter.class);
        JsonAwareResource.setDefault(MenuItem.class, MenuItemAdapter.class);
        JsonAwareResource.setDefault(CMSTheme.class, ThemeAdapter.class);
    }

    @BeforeClass
    @Atomic(mode = TxMode.WRITE)
    public static void initObjects() {
        ensure();
    }

    protected JsonElement removeNullKeys(JsonElement elem) {
        Gson builder = new GsonBuilder().create();
        return builder.toJsonTree(elem);
    }

    public WebTarget getCmsTarget() {
        return target("cms");
    }

    public WebTarget getSitesTarget() {
        return getCmsTarget().path("sites");
    }

    public WebTarget getSiteTarget(Site site) {
        return getSitesTarget().path(site.getExternalId());
    }

    public WebTarget getSitePostsTarget(Site site) {
        return getSiteTarget(site).path("posts");
    }

    public WebTarget getSitePagesTarget(Site site) {
        return getSiteTarget(site).path("pages");
    }

    public WebTarget getSiteCategoriesTarget(Site site) {
        return getSiteTarget(site).path("categories");
    }

    public WebTarget getSiteMenusTarget(Site site) {
        return getSiteTarget(site).path("menus");
    }

    public WebTarget getPostsTarget() {
        return getCmsTarget().path("posts");
    }

    public WebTarget getPostTarget(Post post) {
        return getPostsTarget().path(post.getExternalId());
    }

    public WebTarget getPostVersionsTarget(Post post) {
        return getPostTarget(post).path("versions");
    }

    public WebTarget getVersionsTarget() {
        return getCmsTarget().path("versions");
    }

    public WebTarget getVersionTarget(PostContentRevision version) {
        return getVersionsTarget().path(version.getExternalId());
    }

    public WebTarget getPagesTarget() {
        return getCmsTarget().path("pages");
    }

    public WebTarget getPageTarget(Page page) {
        return getPagesTarget().path(page.getExternalId());
    }

    public WebTarget getCategoriesTarget() {
        return getCmsTarget().path("categories");
    }

    public WebTarget getCategoryTarget(Category category) {
        return getCategoriesTarget().path(category.getExternalId());
    }

    public WebTarget getMenusTarget() {
        return getCmsTarget().path("menus");
    }

    public WebTarget getMenuTarget(Menu menu) {
        return getMenusTarget().path(menu.getExternalId());
    }

    public WebTarget getMenuMenuItemsTarget(Menu menu) {
        return getMenuTarget(menu).path("menuItems");
    }

    public WebTarget getMenuItemsTarget() {
        return getCmsTarget().path("menuItems");
    }

    public WebTarget getMenuItemTarget(MenuItem menuItem) {
        return getMenuItemsTarget().path(menuItem.getExternalId());
    }

    public WebTarget getThemesTarget() {
        return getCmsTarget().path("themes");
    }

    public WebTarget getThemeTarget(CMSTheme theme) {
        return getThemesTarget().path(theme.getExternalId());
    }
}
