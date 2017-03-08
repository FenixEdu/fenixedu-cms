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

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.domain.component.*;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;

/**
 * SiteTemplate for that is an example of a blog having a dummy content.
 */
public class BlogSiteBuilder extends BlogSiteBuilder_Base {

    private Post about;
    
    public static BlogSiteBuilder getInstance(){
        return Bennu.getInstance().getSiteBuildersSet().stream().filter(siteBuilder -> siteBuilder instanceof BlogSiteBuilder)
                .map(siteBuilder -> (BlogSiteBuilder) siteBuilder)
                .findFirst().orElseGet(()->new BlogSiteBuilder());
    }

    private BlogSiteBuilder() {
        super();
        this.setSlug(BlogSiteBuilder.class.getSimpleName());
        Bennu.getInstance().getSiteBuildersSet().add(this);
    }
    
    @Override
    public Site create(LocalizedString name, LocalizedString description) {
        Site site = super.create(name,description);
        
        site.setTheme(getTheme());
        makeCategory(site);
        makePosts(site);
                
        Page homepage = makeHomepage(site);
        Page about = makeAboutPage(site);
        Page postPage = makePostPage(site);
        Page categories = makeCategories(site);
        Page category = makeCategoryPage(site);

        Menu makeMenu = new Menu(site, new LocalizedString(I18N.getLocale(), "Menu"));

        MenuItem menuItem = new MenuItem(makeMenu);
        menuItem.setName(new LocalizedString(I18N.getLocale(), "Homepage"));
        menuItem.setPage(homepage);
        makeMenu.add(menuItem);

        menuItem = new MenuItem(makeMenu);
        menuItem.setName(new LocalizedString(I18N.getLocale(), "About"));
        menuItem.setPage(about);
        makeMenu.add(menuItem);

        menuItem = new MenuItem(makeMenu);
        menuItem.setName(new LocalizedString(I18N.getLocale(), "Categories"));
        menuItem.setPage(categories);
        makeMenu.add(menuItem);

        site.setInitialPage(homepage);
        
        return site;
    }

    private void makeCategory(Site site) {
        Category category = new Category(site, new LocalizedString(I18N.getLocale(), "Welcome Posts"));

        category = new Category(site, new LocalizedString(I18N.getLocale(), "Random Text"));
    }

    private void makePosts(Site site) {
        Post post = new Post(site);
        post.setActive(true);
        post.addCategories(site.categoryForSlug("welcome-posts"));
        post.setName(new LocalizedString(I18N.getLocale(), "Welcome to FenixEdu CMS"));
        post.setBody(new LocalizedString(I18N.getLocale(),
                                         "This is a simple blog that was generated for you, so "
                                         + "you can start understanding how the CMS works. Access to admin space to alter "
                                         + "stuff around or to create new posts."));
        SiteActivity.createdPost(post, Authenticate.getUser());
        post = new Post(site);
        post.setActive(true);
        post.addCategories(site.categoryForSlug("random-text"));
        post.setName(new LocalizedString(I18N.getLocale(), "This is a post"));
        post.setBody(new LocalizedString(I18N.getLocale(), "Lorem ipsum dolor sit amet, consectetur adipiscing "
                + "elit. Nullam tempor, felis eget pulvinar fringilla, dui orci dignissim mi, sit amet "
                + "tincidunt dui purus in nulla. Etiam sit amet dolor at augue ullamcorper volutpat. Integer "
                + "in tellus quam. Ut rutrum eget enim vel suscipit. Curabitur ornare, mauris at volutpat "
                + "congue, elit dolor imperdiet ligula, eget malesuada lacus lacus eu tortor. Maecenas ac "
                + "lacus nisl. Aliquam erat volutpat."));
        SiteActivity.createdPost(post, Authenticate.getUser());
        post = new Post(site);
        post.setActive(true);
        post.addCategories(site.categoryForSlug("random-text"));
        post.setName(new LocalizedString(I18N.getLocale(), "This is a another post"));
        post.setBody(new LocalizedString(I18N.getLocale(), "Curabitur quis erat gravida, rhoncus leo a, vehicula mi."
                + " Etiam a ante sit amet libero feugiat pellentesque sit amet porttitor augue. Cras tempor quis metus"
                + " non tincidunt. Duis non nulla aliquet, hendrerit metus nec, ullamcorper lectus. Donec posuere et "
                + "dui a ultricies. Fusce tempor hendrerit velit, sed facilisis diam venenatis ut. Suspendisse feugiat"
                + " ullamcorper mattis. Ut feugiat sed augue feugiat elementum. In quis augue viverra, ultricies elit et, "
                + "mollis tellus. Phasellus consequat rhoncus sem, sit amet consectetur risus pharetra laoreet. Integer at "
                + "tristique elit. Suspendisse arcu nunc, vestibulum non nulla ut, feugiat varius ipsum. Nulla sagittis dui "
                + "accumsan auctor pulvinar."));
        SiteActivity.createdPost(post, Authenticate.getUser());
        about = new Post(site);
        post.setActive(true);
        about.setName(new LocalizedString(I18N.getLocale(), "About "));
        about.setBody(new LocalizedString(I18N.getLocale(), "This is a simple page show how to create a page about you."));
        SiteActivity.createdPost(post, Authenticate.getUser());
    }

    private Page makeHomepage(Site site) {
        Page page = new Page(site, new LocalizedString(I18N.getLocale(), "Homepage"));
        page.setPublished(true);
        page.addComponents(Component.forType(ListPosts.class));
        page.setTemplate(site.getTheme().templateForType("posts"));
        return page;
    }

    private Page makeAboutPage(Site site) {
        Page page = new Page(site, new LocalizedString(I18N.getLocale(), "About"));
        page.setPublished(true);
        StaticPost components = new StaticPost(about);
        page.addComponents(components);
        page.setTemplate(site.getTheme().templateForType("view"));
        return page;
    }

    private Page makePostPage(Site site) {
        Page page = new Page(site, new LocalizedString(I18N.getLocale(), "View"));
        page.setPublished(true);
        page.addComponents(Component.forType(ViewPost.class));
        page.setTemplate(site.getTheme().templateForType("view"));
        return page;
    }

    private Page makeCategories(Site site) {
        Page page = new Page(site, new LocalizedString(I18N.getLocale(), "Categories"));
        page.setPublished(true);
        page.addComponents(Component.forType(ListOfCategories.class));
        page.setTemplate(site.getTheme().templateForType("categories"));
        return page;
    }

    private Page makeCategoryPage(Site site) {
        Page page = new Page(site, new LocalizedString(I18N.getLocale(), "Category"));
        page.setPublished(true);
        page.addComponents(new ListCategoryPosts(site.categoryForSlug("random-text")));
        page.setTemplate(site.getTheme().templateForType("category"));
        return page;
    }
}
