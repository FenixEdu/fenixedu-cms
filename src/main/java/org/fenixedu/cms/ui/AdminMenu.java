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

import static java.util.stream.Collectors.toList;
import static org.fenixedu.cms.domain.PermissionEvaluation.canDoThis;
import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.EDIT_PRIVILEGED_MENU;

import java.util.function.Predicate;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.MenuItem;
import org.fenixedu.cms.domain.PermissionsArray.Permission;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.StaticPost;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import pt.ist.fenixframework.FenixFramework;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/menus")
public class AdminMenu {

    @Autowired
    AdminMenusService service;

    private static final String JSON = "application/json;charset=utf-8";
    private static final JsonParser JSON_PARSER = new JsonParser();

    @RequestMapping(value = "{siteSlug}", method = RequestMethod.GET)
    public String menus(Model model, @PathVariable String siteSlug) {
        Site site = Site.fromSlug(siteSlug);
        AdminSites.canEdit(site);
        ensureCanDoThis(site, Permission.LIST_MENUS);
        boolean canManagePrivileged = canDoThis(site, EDIT_PRIVILEGED_MENU);
        model.addAttribute("site", site);
        model.addAttribute("menus", site.getOrderedMenusSet().stream()
            .filter(menu -> !menu.getPrivileged() || canManagePrivileged)
            .collect(toList()));
        return "fenixedu-cms/menus";
    }

    @RequestMapping(value = "{slug}/create", method = RequestMethod.POST)
    public RedirectView createMenu(@PathVariable String slug, @RequestParam LocalizedString name) {
        Site site = Site.fromSlug(slug);
        ensureCanDoThis(site, Permission.LIST_MENUS, Permission.EDIT_MENU, Permission.CREATE_MENU);
        Menu menu = service.createMenu(site, name);
        return new RedirectView("/cms/menus/" + site.getSlug() + "/" + menu.getSlug() + "/edit", true);
    }

    @RequestMapping(value = "{slugSite}/{slugMenu}/delete", method = RequestMethod.POST)
    public RedirectView delete(@PathVariable String slugSite, @PathVariable String slugMenu) {
        FenixFramework.atomic(() -> {
            Site site = Site.fromSlug(slugSite);
            AdminSites.canEdit(site);
            ensureCanDoThis(site, Permission.LIST_MENUS, Permission.EDIT_MENU,
                            Permission.DELETE_MENU);
            Menu menu = site.menuForSlug(slugMenu);
            if(menu.getPrivileged()) {
                ensureCanDoThis(site, EDIT_PRIVILEGED_MENU,
                                Permission.DELETE_PRIVILEGED_MENU);
            }
            menu.delete();
        });
        return new RedirectView("/cms/menus/" + slugSite, true);
    }
    @RequestMapping(value = "{slugSite}/{slugMenu}/up", method = RequestMethod.POST)
    public RedirectView moveMenuUp(Model model, @PathVariable String slugSite, @PathVariable String slugMenu) {
        FenixFramework.atomic(() -> {
            Site site = Site.fromSlug(slugSite);
            AdminSites.canEdit(site);
            ensureCanDoThis(site, Permission.LIST_MENUS, Permission.EDIT_MENU);
            Menu menu = site.menuForSlug(slugMenu);
            if(menu.getPrivileged()) {
                ensureCanDoThis(site, EDIT_PRIVILEGED_MENU,
                        Permission.DELETE_PRIVILEGED_MENU);
            }
            Integer oldOrder = menu.getOrder();
            if(oldOrder>1) {
                site.getOrderedMenusSet().stream().filter(m -> m.getOrder() == oldOrder - 1).forEach(m -> m.setOrder(oldOrder));
                menu.setOrder(oldOrder - 1);
            }
        });
        return new RedirectView("/cms/menus/" + slugSite, true);
    }


    @RequestMapping(value = "{slugSite}/{slugMenu}/down", method = RequestMethod.POST)
    public RedirectView moveMenuDown(Model model, @PathVariable String slugSite, @PathVariable String slugMenu) {
        FenixFramework.atomic(() -> {
            Site site = Site.fromSlug(slugSite);
            AdminSites.canEdit(site);
            ensureCanDoThis(site, Permission.LIST_MENUS, Permission.EDIT_MENU);
            Menu menu = site.menuForSlug(slugMenu);
            if(menu.getPrivileged()) {
                ensureCanDoThis(site, EDIT_PRIVILEGED_MENU,
                        Permission.DELETE_PRIVILEGED_MENU);
            }
            Integer oldOrder = menu.getOrder();
            if(oldOrder<site.getMenusSet().size()) {
                site.getOrderedMenusSet().stream().filter(m -> m.getOrder() == oldOrder + 1).forEach(m -> m.setOrder(oldOrder));
                menu.setOrder(oldOrder + 1);
            }
        });
        return new RedirectView("/cms/menus/" + slugSite, true);
    }


    @RequestMapping(value = "{slugSite}/{slugMenu}/edit", method = RequestMethod.GET)
    public String viewEditMenu(Model model, @PathVariable String slugSite, @PathVariable String slugMenu) {
        Site site = Site.fromSlug(slugSite);
        AdminSites.canEdit(site);
        ensureCanDoThis(site, Permission.LIST_MENUS, Permission.EDIT_MENU);
        Menu menu = site.menuForSlug(slugMenu);
        if(menu.getPrivileged()) {
            ensureCanDoThis(site, EDIT_PRIVILEGED_MENU);
        }
        model.addAttribute("site", site);
        model.addAttribute("menu", menu);
        return "fenixedu-cms/editMenu";
    }

    private final Predicate<MenuItem> isStaticPage = menuItem -> menuItem.getPage() != null
            && menuItem.getPage().getComponentsSet().stream().filter(StaticPost.class::isInstance)
            .map(component -> ((StaticPost) component).getPost()).filter(post -> post != null).findFirst().isPresent();

    @RequestMapping(value = "{slugSite}/{slugMenu}/data", method = RequestMethod.GET, produces = JSON)
    public @ResponseBody String menuData(@PathVariable String slugSite, @PathVariable String slugMenu) {
        Site site = Site.fromSlug(slugSite);
        AdminSites.canEdit(site);
        ensureCanDoThis(site, Permission.LIST_MENUS, Permission.EDIT_MENU);
        Menu menu = site.menuForSlug(slugMenu);
        if(menu.getPrivileged()) {
            ensureCanDoThis(menu.getSite(), EDIT_PRIVILEGED_MENU);
        }
        JsonObject data = new JsonObject();
        JsonObject pages = new JsonObject();
        site.getSortedPages().stream()
                .forEach(page-> pages.add(page.getSlug(), service.serializePage(page)));
        data.add("menu", service.serializeMenu(menu));
        data.add("pages", pages);
        return data.toString();
    }

    @RequestMapping(value = "{slugSite}/{slugMenu}/edit", method = RequestMethod.POST, consumes = JSON, produces = JSON)
    public @ResponseBody String editMenu(@PathVariable String slugSite, @PathVariable String slugMenu, HttpEntity<String> http) {
        Site site = Site.fromSlug(slugSite);
        FenixFramework.atomic(() -> {
            ensureCanDoThis(site, Permission.LIST_MENUS, Permission.EDIT_MENU);
            JsonObject json = JSON_PARSER.parse(http.getBody()).getAsJsonObject();
            service.processMenuChanges(site.menuForSlug(slugMenu), json);
            AdminSites.canEdit(site);
        });
        return menuData(slugSite, slugMenu);
    }

}
