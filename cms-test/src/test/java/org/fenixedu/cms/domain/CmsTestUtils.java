package org.fenixedu.cms.domain;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.domain.PermissionsArray.Permission;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;

import java.util.EnumSet;
import java.util.Locale;

import static junit.framework.TestCase.assertNotNull;

public class CmsTestUtils {
    
      public static void setUserAsManager(User user){
          CmsSettings settings = CmsSettings.getInstance();
          assertNotNull(settings); // If you know what I know, this assert seems perfectly sane
          PersistentGroup pgroup = Group.users(user).toPersistentGroup();
          assertNotNull(pgroup);
          settings.setSettingsManagers(pgroup);
    }
    
    public static LocalizedString createLocalizedString(String base) {
        return new LocalizedString(I18N.getLocale(), base);
    }

    public static User createAuthenticatedUser(String username) {
        return createAuthenticatedUser(username, "name", "familyName", "mail@fenixedu.org");
    }

    public static User createAuthenticatedUser(String username, String name, String familyName, String email) {
        User user = new User(username, new UserProfile(name, familyName, name + " " + familyName, email, Locale.getDefault()));
        Authenticate.mock(user);
        return user;
    }

    public static Site createSite(User user, String methodName) {
        LocalizedString siteName =
                new LocalizedString(Locale.UK, "site name uk " + methodName).with(Locale.US, "site name us " + methodName);
        LocalizedString siteDescription =
                new LocalizedString(Locale.UK, "site description uk " + methodName).with(Locale.US, "site description us "
                        + methodName);

        Site site = new Site(siteName, siteDescription);

        Role adminRole = new Role(createRoleTemplate("admin", bootstrapAdminPermissions()), site);
        adminRole.setGroup(Group.users(user));

        return site;
    }

    public static Post createPost(Site site, String methodName) {
        Post post = new Post(site);
        LocalizedString postName =
                new LocalizedString(Locale.UK, "post name uk " + methodName).with(Locale.US, "post name us " + methodName);
        LocalizedString postBody =
                new LocalizedString(Locale.UK, "post body uk " + methodName).with(Locale.US, "post body us " + methodName);
        post.setName(postName);
        post.setBody(postBody);

        return post;
    }

    public static PostContentRevision createVersion(Post post, String methodName) {
        LocalizedString versionBody =
                new LocalizedString(Locale.UK, "version body uk " + methodName).with(Locale.US, "version body us " + methodName);
        post.setBody(versionBody);

        return post.getLatestRevision();
    }

    public static Page createPage(Site site, String methodName) {
        LocalizedString pageName =
                new LocalizedString(Locale.UK, "page name uk " + methodName).with(Locale.US, "page name us " + methodName);
        Page page = new Page(site, pageName);

        return page;
    }

    public static Category createCategory(Site site, String methodName) {
        LocalizedString categoryName =
                new LocalizedString(Locale.UK, "category name uk " + methodName)
        .with(Locale.US, "category name us " + methodName);
        Category category = new Category(site, categoryName);

        return category;
    }

    public static Menu createMenu(Site site, String methodName) {
        LocalizedString menuName =
                new LocalizedString(Locale.UK, "menu name uk " + methodName).with(Locale.US, "menu name us " + methodName);
        Menu menu = new Menu(site, menuName);

        return menu;
    }

    public static MenuItem createMenuItem(Menu menu, String methodName) {
        MenuItem menuItem = new MenuItem(menu);
        LocalizedString menuItemName =
                new LocalizedString(Locale.UK, "menuItem name uk " + methodName)
        .with(Locale.US, "menuItem name us " + methodName);
        menuItem.setName(menuItemName);

        return menuItem;
    }

    public static CMSTheme createTheme(String methodName) {
        CMSTheme theme = new CMSTheme();
        theme.setBennu(Bennu.getInstance());
        theme.setName("theme name " + methodName);
        theme.setDescription("theme description " + methodName);
        theme.setType("theme type " + methodName);

        return theme;
    }
    
    
    public static RoleTemplate createRoleTemplate(String name, EnumSet<Permission> permissions){
        RoleTemplate roletemplate = new RoleTemplate();
        roletemplate.setName(createLocalizedString(name));
        roletemplate.setPermissions(new PermissionsArray(permissions));
        return roletemplate;
    }
    
    
    public static EnumSet<Permission> bootstrapAdminPermissions(){
        return EnumSet.of(Permission.CREATE_POST, Permission.CREATE_PAGE, Permission.SEE_POSTS,
            Permission.SEE_PRIVATE_POSTS,
            Permission.SEE_PAGES, Permission.SEE_PAGE_COMPONENTS, Permission.DELETE_OTHERS_POSTS, Permission.DELETE_PAGE,
            Permission.DELETE_POSTS, Permission.DELETE_PRIVATE_POSTS, Permission.DELETE_POSTS_PUBLISHED,
            Permission.EDIT_OTHERS_POSTS, Permission.EDIT_PAGE, Permission.EDIT_POSTS, Permission.EDIT_POSTS_PUBLISHED,
            Permission.EDIT_SITE_INFORMATION, Permission.LIST_CATEGORIES, Permission.EDIT_CATEGORY, Permission.DELETE_CATEGORY,
            Permission.CREATE_CATEGORY, Permission.MANAGE_ANALYTICS, Permission.MANAGE_ROLES, Permission.PUBLISH_PAGES,
            Permission.PUBLISH_POSTS, Permission.PUBLISH_SITE, Permission.CREATE_MENU, Permission.DELETE_MENU,
            Permission.LIST_MENUS, Permission.EDIT_MENU, Permission.CREATE_MENU_ITEM, Permission.DELETE_MENU_ITEM,
            Permission.EDIT_MENU_ITEM, Permission.CHANGE_PATH_PAGES, Permission.CHOOSE_PATH_AND_FOLDER,
            Permission.EDIT_SITE_INFORMATION, Permission.CHOOSE_DEFAULT_PAGE);
    }

}
