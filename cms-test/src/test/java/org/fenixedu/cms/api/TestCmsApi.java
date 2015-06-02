package org.fenixedu.cms.api;

import java.util.Locale;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;
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
import org.fenixedu.bennu.core.rest.DomainObjectParamConverter;
import org.fenixedu.bennu.core.rest.JsonAwareResource;
import org.fenixedu.bennu.core.security.Authenticate;
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
import org.fenixedu.commons.i18n.LocalizedString;
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

    protected static boolean done = false;

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
                ThemeResource.class);
    }

    public static void ensure() {
        if (!done) {
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

            done = true;
        }
    }

    @BeforeClass
    @Atomic(mode = TxMode.WRITE)
    public static void initObjects() {
        Bennu.getInstance().getUserSet().forEach(u -> Bennu.getInstance().getUserSet().remove(u));
        ensure();
    }

    protected JsonElement removeNullKeys(JsonElement elem) {
        Gson builder = new GsonBuilder().create();
        return builder.toJsonTree(elem);
    }

    protected User createAuthenticatedUser(String username) {
        return createAuthenticatedUser(username, "name", "familyName", "mail@fenixedu.org");
    }

    protected User createAuthenticatedUser(String username, String name, String familyName, String email) {
        User user = new User(username, new UserProfile(name, familyName, name + " " + familyName, email, Locale.getDefault()));
        Authenticate.mock(user);
        return user;
    }

    public Site createSite(User user, String methodName) {
        LocalizedString siteName =
                new LocalizedString(Locale.UK, "site name uk " + methodName).with(Locale.US, "site name us " + methodName);
        LocalizedString siteDescription =
                new LocalizedString(Locale.UK, "site description uk " + methodName).with(Locale.US, "site description us "
                        + methodName);

        Site site = new Site(siteName, siteDescription);

        site.setCanAdminGroup(UserGroup.of(user));
        return site;
    }

    public Post createPost(Site site, String methodName) {
        Post post = new Post(site);
        LocalizedString postName =
                new LocalizedString(Locale.UK, "post name uk " + methodName).with(Locale.US, "post name us " + methodName);
        LocalizedString postBody =
                new LocalizedString(Locale.UK, "post body uk " + methodName).with(Locale.US, "post body us " + methodName);
        post.setName(postName);
        post.setBody(postBody);

        return post;
    }

    public PostContentRevision createVersion(Post post, String methodName) {
        LocalizedString versionBody =
                new LocalizedString(Locale.UK, "version body uk " + methodName).with(Locale.US, "version body us " + methodName);
        post.setBody(versionBody);

        return post.getLatestRevision();
    }

    public Page createPage(Site site, String methodName) {
        Page page = new Page(site);
        LocalizedString pageName =
                new LocalizedString(Locale.UK, "page name uk " + methodName).with(Locale.US, "page name us " + methodName);
        page.setName(pageName);

        return page;
    }

    public Category createCategory(Site site, String methodName) {
        Category category = new Category(site);
        LocalizedString categoryName =
                new LocalizedString(Locale.UK, "category name uk " + methodName)
        .with(Locale.US, "category name us " + methodName);
        category.setName(categoryName);

        return category;
    }

    public Menu createMenu(Site site, String methodName) {
        Menu menu = new Menu(site);
        LocalizedString menuName =
                new LocalizedString(Locale.UK, "menu name uk " + methodName).with(Locale.US, "menu name us " + methodName);
        menu.setName(menuName);

        return menu;
    }

    public MenuItem createMenuItem(Menu menu, String methodName) {
        MenuItem menuItem = new MenuItem(menu);
        LocalizedString menuItemName =
                new LocalizedString(Locale.UK, "menuItem name uk " + methodName)
        .with(Locale.US, "menuItem name us " + methodName);
        menuItem.setName(menuItemName);

        return menuItem;
    }

    public CMSTheme createTheme(String methodName) {
        CMSTheme theme = new CMSTheme();
        theme.setBennu(Bennu.getInstance());
        theme.setName("theme name " + methodName);
        theme.setDescription("theme description " + methodName);
        theme.setType("theme type " + methodName);

        return theme;
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
