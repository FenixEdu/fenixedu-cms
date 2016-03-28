package org.fenixedu.cms.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.fenixedu.bennu.core.groups.ManualGroupRegister;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.rest.DomainExceptionMapper;
import org.fenixedu.bennu.core.rest.DomainObjectParamConverter;
import org.fenixedu.bennu.core.rest.JsonAwareResource;
import org.fenixedu.bennu.core.rest.JsonBodyReaderWriter;
import org.fenixedu.cms.api.json.*;
import org.fenixedu.cms.api.resource.*;
import org.fenixedu.cms.domain.*;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import java.util.stream.Stream;

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
                ThemeResource.class, DomainExceptionMapper.class, JsonBodyReaderWriter.class);
    }

    public static void ensure() {
        ManualGroupRegister.ensure();
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

    public WebTarget getSitePostsTargetWithCategory(Site site, Category category) {
        return getSitePostsTarget(site).queryParam("category", category.getExternalId());
    }

    public WebTarget getSitePostsTargetWithCategories(Site site, Category... category) {
        return getSitePostsTarget(site).queryParam("category", Stream.of(category).map(Category::getExternalId).toArray());
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
