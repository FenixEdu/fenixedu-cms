package org.fenixedu.cms.ui;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.StaticPost;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ist.fenixframework.Atomic;

/**
 * Created by borgez on 03-08-2015.
 */
@Service
public class AdminPagesService {

    @Autowired
    AdminPostsService postsService;

    @Autowired
    AdminMenusService menusService;

    @Atomic(mode = Atomic.TxMode.WRITE)
    public Page createPageAndPost(LocalizedString name, Site site) {
	AdminSites.canEdit(site);
	Post post = postsService.createPost(site, name);
	Page page = new Page(site, Post.sanitize(name));
	page.addComponents(new StaticPost(post));
	page.setTemplateType("view");
	page.setPublished(false);
	page.setCanViewGroup(site.getCanViewGroup());
	return page;
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void processChanges(Site site, Page page, JsonObject editData) {
	JsonObject pageJson = editData.get("post").getAsJsonObject();
	JsonArray menusJson = editData.get("menus").getAsJsonArray();

	Post post = page.getStaticPost().get();
	postsService.processPostChanges(site, page.getStaticPost().get(), pageJson);
	page.setName(Post.sanitize(post.getName()));
	page.setCanViewGroup(post.getCanViewGroup());
	page.setPublished(post.getActive());
	page.setSlug(post.getSlug());
	menusJson.forEach(jsonElement -> {
	    JsonObject menuJson = jsonElement.getAsJsonObject();
	    menusService.processMenuChanges(site.menuForSlug(menuJson.get("key").getAsString()), menuJson.getAsJsonObject());
	});
    }

    public JsonObject serializePage(Page page) {
	return postsService.serializePost(page.getStaticPost().get());
    }

    public JsonObject serializeMenu(Menu menu) {
	return menusService.serializeMenu(menu);
    }

}
