package org.fenixedu.cms.domain;

import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;

/**
 * SiteTemplate for that is an example of a blog having a dummy content.
 */
@RegisterSiteTemplate(name="Blog", description="A simple blog", type="blog")
public class BlogTemplate implements SiteTemplate {

    private Post about;

    /**
     * Populates a site with the structure of a blog.
     * Including some pages, menus, posts and categories.
     * 
     * @param site
     *            the site to be populated.
     */
    @Override
    public void makeIt(Site site) {
        
        site.setTheme(CMSTheme.forType("cms-default-theme"));
        makeCategory(site);
        makePosts(site);
        Page homepage = makeHomepage(site);
        Page about = makeAboutPage(site);
        Page postPage = makePostPage(site);
        Page categories = makeCategories(site);
        Page category = makeCategoryPage(site);
        
        Menu makeMenu = new Menu();
        
        makeMenu.setName(new LocalizedString(I18N.getLocale(),"Menu"));
        makeMenu.setSite(site);
        
        MenuItem menuItem = new MenuItem();
        menuItem.setName(new LocalizedString(I18N.getLocale(),"Homepage"));
        menuItem.setPage(homepage);
        makeMenu.add(menuItem);
        
        menuItem = new MenuItem();
        menuItem.setName(new LocalizedString(I18N.getLocale(),"About"));
        menuItem.setPage(about);
        makeMenu.add(menuItem);
        
        menuItem = new MenuItem();
        menuItem.setName(new LocalizedString(I18N.getLocale(),"Categories"));
        menuItem.setPage(categories);
        makeMenu.add(menuItem);
        
        MenuComponent mc = new MenuComponent();
        mc.setMenu(makeMenu);
        homepage.addComponents(mc);
        
        mc = new MenuComponent();
        mc.setMenu(makeMenu);
        about.addComponents(mc);
        
        mc = new MenuComponent();
        mc.setMenu(makeMenu);
        postPage.addComponents(mc);
        
        mc = new MenuComponent();
        mc.setMenu(makeMenu);
        categories.addComponents(mc);
        
        mc = new MenuComponent();
        mc.setMenu(makeMenu);
        category.addComponents(mc);
        
        
    }

    private void makeCategory(Site site) {
        Category category = new Category();
        category.setName(new LocalizedString(I18N.getLocale(),"Welcome Posts"));
        category.setSite(site);
        
        category = new Category();
        category.setName(new LocalizedString(I18N.getLocale(),"Random Text"));
        category.setSite(site);
    }

    private void makePosts(Site site) {
        Post post = new Post();
        post.setSite(site);
        post.setCategory(site.categoryForSlug("welcome-posts"));
        post.setName(new LocalizedString(I18N.getLocale(),"Welcome to FenixEdu CMS"));
        post.setBody(new LocalizedString(I18N.getLocale(),"This is a simple blog that was generated for you, so "
                + "you can start understanding how the CMS works. Access to admin space to alter "
                + "stuff around or to create new posts."));
        for (int i = 0; i < 15; i++) {
        post = new Post();
        post.setSite(site);
        post.setCategory(site.categoryForSlug("random-text"));
        post.setName(new LocalizedString(I18N.getLocale(),"This is a post"));
        post.setBody(new LocalizedString(I18N.getLocale(),"Lorem ipsum dolor sit amet, consectetur adipiscing "
                + "elit. Nullam tempor, felis eget pulvinar fringilla, dui orci dignissim mi, sit amet "
                + "tincidunt dui purus in nulla. Etiam sit amet dolor at augue ullamcorper volutpat. Integer "
                + "in tellus quam. Ut rutrum eget enim vel suscipit. Curabitur ornare, mauris at volutpat "
                + "congue, elit dolor imperdiet ligula, eget malesuada lacus lacus eu tortor. Maecenas ac "
                + "lacus nisl. Aliquam erat volutpat."));
        
        post = new Post();
        post.setSite(site);
        post.setCategory(site.categoryForSlug("random-text"));
        post.setName(new LocalizedString(I18N.getLocale(),"This is a another post"));
        post.setBody(new LocalizedString(I18N.getLocale(),"Curabitur quis erat gravida, rhoncus leo a, vehicula mi."
                + " Etiam a ante sit amet libero feugiat pellentesque sit amet porttitor augue. Cras tempor quis metus"
                + " non tincidunt. Duis non nulla aliquet, hendrerit metus nec, ullamcorper lectus. Donec posuere et "
                + "dui a ultricies. Fusce tempor hendrerit velit, sed facilisis diam venenatis ut. Suspendisse feugiat"
                + " ullamcorper mattis. Ut feugiat sed augue feugiat elementum. In quis augue viverra, ultricies elit et, "
                + "mollis tellus. Phasellus consequat rhoncus sem, sit amet consectetur risus pharetra laoreet. Integer at "
                + "tristique elit. Suspendisse arcu nunc, vestibulum non nulla ut, feugiat varius ipsum. Nulla sagittis dui "
                + "accumsan auctor pulvinar."));
        
        }
        about = new Post();
        about.setSite(site);
        about.setName(new LocalizedString(I18N.getLocale(),"About " + site.getCreatedBy().getUsername()));
        about.setBody(new LocalizedString(I18N.getLocale(),"This is a simple page show how to create a page about you."));
    }

    private Page makeHomepage(Site site) {
        Page page = new Page();
        page.setName(new LocalizedString(I18N.getLocale(),"Homepage"));
        page.setSite(site);
        page.addComponents(new ListPosts());
        page.setTemplate(site.getTheme().templateForType("posts"));
        page.setSlug("");
        return page;
    }
    
    private Page makeAboutPage(Site site) {
        Page page = new Page();
        page.setName(new LocalizedString(I18N.getLocale(),"About"));
        page.setSite(site);
        StaticPost components = new StaticPost();
        components.setPost(about);
        page.addComponents(components);
        page.setTemplate(site.getTheme().templateForType("view"));
        return page;
    }
    
    private Page makePostPage(Site site) {
        Page page = new Page();
        page.setName(new LocalizedString(I18N.getLocale(),"View"));
        page.setSite(site);
        page.addComponents(new ViewPost());
        page.setTemplate(site.getTheme().templateForType("view"));
        return page;
    }
    

    private Page makeCategories(Site site) {
        Page page = new Page();
        page.setName(new LocalizedString(I18N.getLocale(),"Categories"));
        page.setSite(site);
        page.addComponents(new ListOfCategories());
        page.setTemplate(site.getTheme().templateForType("categories"));
        return page;
    }
    

    private Page makeCategoryPage(Site site) {
        Page page = new Page();
        page.setName(new LocalizedString(I18N.getLocale(),"Category"));
        page.setSite(site);
        page.addComponents(new ListCategoryPosts());
        page.setTemplate(site.getTheme().templateForType("category"));
        return page;
    }


}
