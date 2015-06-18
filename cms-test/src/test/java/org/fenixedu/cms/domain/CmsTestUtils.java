package org.fenixedu.cms.domain;

import java.util.Locale;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;
import org.fenixedu.bennu.core.groups.UserGroup;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;

public class CmsTestUtils {

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

        site.setCanAdminGroup(UserGroup.of(user));
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
}
