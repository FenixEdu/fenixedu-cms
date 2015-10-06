package org.fenixedu.cms.ui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.MenuItem;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.PermissionsArray.Permission;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import pt.ist.fenixframework.Atomic;

import static java.util.Optional.ofNullable;
import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;

/**
 * Created by borgez on 03-08-2015.
 */
@Service
public class AdminMenusService {

    @Atomic
    public Menu createMenu(Site site, LocalizedString name) {
	AdminSites.canEdit(site);
	return new Menu(site, name);
    }

    public JsonObject serializeMenu(Menu menu) {
	JsonObject menuJson = new JsonObject();

	menuJson.addProperty("title", Optional.ofNullable(menu.getName())
            .map(LocalizedString::getContent).orElse(menu.getSite().getName().getContent()));
	menuJson.add("name", menu.getName().json());
	menuJson.addProperty("key", menu.getSlug());
	menuJson.addProperty("root", true);
	menuJson.addProperty("folder", true);
    	menuJson.addProperty("privileged", menu.getPrivileged());
	JsonArray child = new JsonArray();
	menu.getToplevelItemsSorted().map(this::serializeMenuItem).forEach(json -> child.add(json));
	menuJson.add("children", child);

	return menuJson;
    }

    private JsonObject serializeMenuItem(MenuItem item) {
	JsonObject root = new JsonObject();
	root.add("title", new JsonPrimitive(item.getName().isEmpty() ? "---" : item.getName().getContent()));
	root.add("name", item.getName().json());
	root.addProperty("key", item.getExternalId());
	root.addProperty("url", item.getUrl());
	root.addProperty("page", ofNullable(item.getPage()).map(Page::getSlug).orElse(null));
	root.addProperty("position", item.getPosition());
	root.addProperty("isFolder", ofNullable(item.getFolder()).orElse(false));

	if (item.getChildrenSet().size() > 0) {
	    JsonArray children = new JsonArray();
	    item.getChildrenSorted().stream().map(this::serializeMenuItem).forEach(children::add);
	    root.add("children", children);
	    root.addProperty("folder", true);
	}

	if(item.getPage() != null) {
	    root.addProperty("use", "page");
	} else if(ofNullable(item.getFolder()).orElse(false)) {
	    root.addProperty("use", "folder");
	    root.addProperty("folder", true);
	} else {
	    root.addProperty("use", "url");
	}

	return root;
    }

    public JsonObject serializePage(Page page) {
	JsonObject pageJson = new JsonObject();
	pageJson.addProperty("slug", page.getSlug());
	pageJson.addProperty("address", page.getAddress());
	pageJson.addProperty("editUrl", page.getEditUrl());
	pageJson.add("name", page.getName().json());
	return pageJson;
    }

    Optional<MenuItem> menuItemForOid(Site site, String menuItemOid) {
	return site.getMenusSet().stream().map(menu->menu.menuItemForOid(menuItemOid)).filter(mi->mi!=null).findFirst();
    }

    void processMenuChanges(Menu menu, JsonObject menuJson) {
      ensureCanDoThis(menu.getSite(), Permission.EDIT_MENU);

      if(menu.getPrivileged()) {
	ensureCanDoThis(menu.getSite(), Permission.EDIT_PRIVILEGED_MENU);
      }

      if(menuJson.get("privileged") != null) {
	boolean newIsPrivileged = menuJson.get("privileged").getAsBoolean();
	if(newIsPrivileged != menu.getPrivileged()) {
	  ensureCanDoThis(menu.getSite(), Permission.EDIT_PRIVILEGED_MENU);
	  if(newIsPrivileged) {
	    ensureCanDoThis(menu.getSite(), Permission.CREATE_PRIVILEGED_MENU);
	  } else {
	    ensureCanDoThis(menu.getSite(), Permission.DELETE_PRIVILEGED_MENU);
	  }
	}
      }

      LocalizedString newName = LocalizedString.fromJson(menuJson.get("name"));
	if(!menu.getName().equals(newName)) {
	    menu.setName(newName);
	}

	allDeletedItems(menu, menuJson).forEach(MenuItem::delete);

	if(menuJson.get("children")!=null && menuJson.get("children").isJsonArray()) {
	    JsonArray children = menuJson.get("children").getAsJsonArray();
	    for (int newPosition = 0; newPosition < children.size(); ++newPosition) {
		processMenuItemChanges(menu, null, children.get(newPosition).getAsJsonObject(), newPosition);
	    }
	}
    }

    void processMenuItemChanges(Menu menu, MenuItem parent, JsonObject menuItemJson, int newPosition) {
	LocalizedString newName = LocalizedString.fromJson(menuItemJson.get("name"));
      	String key = menuItemJson.get("key").getAsString();

	MenuItem menuItem = menuItemForOid(menu.getSite(), key).orElseGet(()-> {
	  ensureCanDoThis(menu.getSite(), Permission.CREATE_MENU_ITEM);
	  return new MenuItem(menu);
	});

	menuItem.setName(newName);

	if (parent != null) {
	  parent.putAt(menuItem, newPosition);
	} else {
	  menu.putAt(menuItem, newPosition);
	}

	switch (menuItemJson.get("use").getAsString()) {
	  case "page":
	    setMenuItemPage(menuItem, menuItemJson);
	    break;
	  case "url":
	    setMenuItemUrl(menuItem, menuItemJson);
	    break;
	  default:
	    setMenuItemFolder(menuItem, menuItemJson);
	    break;
	}

	if (menuItemJson.get("children") != null && menuItemJson.get("children").isJsonArray()) {
	  JsonArray children = menuItemJson.get("children").getAsJsonArray();
	  for (int newChildrenPosition = 0; newChildrenPosition < children.size();
	       ++newChildrenPosition) {
	    processMenuItemChanges(menu, menuItem,
				   children.get(newChildrenPosition).getAsJsonObject(),
				   newChildrenPosition);
	  }
	}
    }

    private void setMenuItemFolder(MenuItem menuItem, JsonObject menuItemJson) {
	if(!menuItem.getFolder()) {
	    menuItem.setFolder(true);
	    menuItem.setUrl(null);
	    menuItem.setPage(null);
	}
    }

    private void setMenuItemUrl(MenuItem menuItem, JsonObject menuItemJson) {
	String newUrl = Optional.ofNullable(menuItemJson).map(json->json.get("url"))
	    .filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsString).orElse("#");
	if(!newUrl.equals(menuItem.getUrl())) {
	    menuItem.setFolder(false);
	    menuItem.setPage(null);
	    menuItem.setUrl(newUrl);
	}
    }

    private void setMenuItemPage(MenuItem menuItem, JsonObject menuItemJson) {
      Site site = menuItem.getMenu().getSite();
      Optional.ofNullable(menuItemJson).map(json -> json.get("page"))
	  .filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsString)
	  .map(pageSlug->site.pageForSlug(pageSlug)).filter(page -> page != menuItem.getPage())
	  .ifPresent(page -> {
	    menuItem.setPage(page);
	    menuItem.setFolder(false);
	    menuItem.setUrl(null);
	  });
    }

    private Set<String> allJsonKeys(JsonObject menuItemJson) {
	Set<String> keys = new HashSet<>();
	if(menuItemJson.get("children") != null && menuItemJson.get("children").isJsonArray()) {
	    for(JsonElement el : menuItemJson.get("children").getAsJsonArray()) {
		keys.add(el.getAsJsonObject().get("key").getAsString());
		keys.addAll(allJsonKeys(el.getAsJsonObject()));
	    }
	}
	return keys;
    }

    public Stream<MenuItem> allDeletedItems(Menu menu, JsonObject rootJson) {
	Set<String> allJsonKeys = allJsonKeys(rootJson);
	return menu.getItemsSet().stream().filter(mi->!allJsonKeys.contains(mi.getExternalId()));
    }
}
