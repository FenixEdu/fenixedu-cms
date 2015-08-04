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
	page.setPublished(true);
	page.setCanViewGroup(site.getCanViewGroup());
	return page;
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void processChanges(Site site, Page page, JsonObject editData) {
	JsonObject pageJson = editData.get("post").getAsJsonObject();
	JsonArray menusJson = editData.get("menus").getAsJsonArray();

	Post post = page.getStaticPost().get();
	postsService.processPostChanges(site, page.getStaticPost().get(), pageJson);
	if(!page.getName().equals(post.getName())) {
	    page.setName(post.getName());
	}
	if(!page.getSlug().equals(post.getSlug())) {
	    page.setSlug(post.getSlug());
	}
	if(!page.getCanViewGroup().equals(post.getCanViewGroup())) {
	    page.setCanViewGroup(post.getCanViewGroup());
	}
	if(!page.getPublished() && post.getActive()) {
	    page.setPublished(true);
	}
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
