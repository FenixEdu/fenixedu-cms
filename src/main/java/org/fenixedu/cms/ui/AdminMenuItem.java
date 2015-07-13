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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.MenuItem;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pt.ist.fenixframework.FenixFramework;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/menus")
public class AdminMenuItem {

    @Autowired
    AdminMenusService service;

    private static final String JSON = "application/json;charset=utf-8";
    private static final JsonParser JSON_PARSER = new JsonParser();

    @RequestMapping(value = "{slugSite}/{slugMenu}/change", method = RequestMethod.GET)
    public String viewEditMenu(Model model, @PathVariable String slugSite, @PathVariable String slugMenu) {
        Site site = Site.fromSlug(slugSite);
        AdminSites.canEdit(site);
        model.addAttribute("site", site);
        model.addAttribute("menu", site.menuForSlug(slugMenu));
        return "fenixedu-cms/editMenu";
    }

    @RequestMapping(value = "{slugSite}/{slugMenu}/data", method = RequestMethod.GET, produces = JSON)
    public @ResponseBody String menuData(@PathVariable String slugSite, @PathVariable String slugMenu) {
        Site site = Site.fromSlug(slugSite);
        AdminSites.canEdit(site);
        Menu menu = site.menuForSlug(slugMenu);
        JsonObject data = new JsonObject();
        JsonObject pages = new JsonObject();
        site.getSortedPages().forEach(page-> pages.add(page.getSlug(), service.serializePage(page)));
        data.add("menu", service.serializeMenu(menu));
        data.add("pages", pages);
        return data.toString();
    }

    @RequestMapping(value = "{slugSite}/{slugMenu}/edit", method = RequestMethod.POST, consumes = JSON, produces = JSON)
    public @ResponseBody String editMenu(@PathVariable String slugSite, @PathVariable String slugMenu, HttpEntity<String> httpEntity) {
        Site site = Site.fromSlug(slugSite);
        AdminSites.canEdit(site);
        Menu menu = site.menuForSlug(slugMenu);
        JsonObject menuRoot = JSON_PARSER.parse(httpEntity.getBody()).getAsJsonObject();

        FenixFramework.atomic(()->{

            LocalizedString newName = LocalizedString.fromJson(menuRoot.get("name"));
            if(!menu.getName().equals(newName)) {
                menu.setName(newName);
            }

            service.allDeletedItems(menu, menuRoot).forEach(MenuItem::delete);

            if(menuRoot.get("children")!=null && menuRoot.get("children").isJsonArray()) {
                JsonArray children = menuRoot.get("children").getAsJsonArray();
                for (int newPosition = 0; newPosition < children.size(); ++newPosition) {
                    service.processMenuItemChanges(menu, null, children.get(newPosition).getAsJsonObject(), newPosition);
                }
            }

        });

        return menuData(slugSite, slugMenu);
    }

}
