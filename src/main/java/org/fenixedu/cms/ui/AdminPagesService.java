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
package org.fenixedu.cms.ui;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.signals.DomainObjectEvent;
import org.fenixedu.bennu.signals.Signal;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.PermissionEvaluation;
import org.fenixedu.cms.domain.PermissionsArray.Permission;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.SiteActivity;
import org.fenixedu.cms.domain.component.StaticPost;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
      	SiteActivity.createdPage(page, Authenticate.getUser());
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
			PermissionEvaluation.ensureCanDoThis(site, Permission.CHANGE_PATH_PAGES);
			page.setSlug(post.getSlug());
		}
		if(!page.getCanViewGroup().equals(post.getCanViewGroup())) {
			page.setCanViewGroup(post.getCanViewGroup());
		}
		if(!page.getPublished() && post.getActive()) {
			page.setPublished(true);
		}
		if(PermissionEvaluation.canDoThis(site, Permission.LIST_MENUS, Permission.EDIT_MENU)) {
			menusJson.forEach(jsonElement -> {
				JsonObject menuJson = jsonElement.getAsJsonObject();
				menusService.processMenuChanges(site.menuForSlug(menuJson.get("key").getAsString()),
						menuJson.getAsJsonObject());
			});
		}
		SiteActivity.editedPage(page,Authenticate.getUser());
		Signal.emit(Page.SIGNAL_EDITED, new DomainObjectEvent<>(page));
	}

    public JsonObject serializePage(Page page) {
	return postsService.serializePost(page.getStaticPost().get());
    }

    public JsonObject serializeMenu(Menu menu) {
	return menusService.serializeMenu(menu);
    }

}
