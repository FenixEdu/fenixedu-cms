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
package org.fenixedu.cms.api.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.api.json.DateTimeViewer;
import org.fenixedu.bennu.core.api.json.LocalizedStringViewer;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.signals.DomainObjectEvent;
import org.fenixedu.bennu.core.signals.Signal;
import org.fenixedu.cms.domain.MenuItem;
import org.fenixedu.commons.i18n.LocalizedString;

import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.*;

@DefaultJsonAdapter(MenuItem.class)
public class MenuItemAdapter implements JsonAdapter<MenuItem> {

    @Override
    public MenuItem create(JsonElement arg0, JsonBuilder arg1) {
        // Depends on Menu, implemented in {@MenuResource.createMenuItemFromJson}
        return null;
    }

    @Override
    public MenuItem update(JsonElement json, MenuItem menuItem, JsonBuilder ctx) {
        ensureCanDoThis(menuItem.getMenu().getSite(), LIST_MENUS, EDIT_MENU, EDIT_MENU_ITEM);
        JsonObject jObj = json.getAsJsonObject();

        if (jObj.has("position") && !jObj.get("position").isJsonNull()) {
            menuItem.setPosition(jObj.get("position").getAsInt());
        }

        if (jObj.has("name") && !jObj.get("name").isJsonNull() && jObj.get("name").isJsonObject()) {
            menuItem.setName(LocalizedString.fromJson(jObj.get("name")));
        }

        if (jObj.has("url") && !jObj.get("url").isJsonNull()) {
            menuItem.setUrl(jObj.get("url").getAsString());
        }

        if (jObj.has("folder") && !jObj.get("folder").isJsonNull()) {
            menuItem.setFolder(jObj.get("folder").getAsBoolean());
        }

        Signal.emit(MenuItem.SIGNAL_EDITED, new DomainObjectEvent<>(menuItem));
        return menuItem;
    }

    @Override
    public JsonElement view(MenuItem menuItem, JsonBuilder ctx) {
        ensureCanDoThis(menuItem.getMenu().getSite(), LIST_MENUS);
        JsonObject json = new JsonObject();

        json.addProperty("id", menuItem.getExternalId());
        json.add("name", ctx.view(menuItem.getName(), LocalizedStringViewer.class));
        json.add("creationDate", ctx.view(menuItem.getCreationDate(), DateTimeViewer.class));
        json.addProperty("position", menuItem.getPosition());
        json.addProperty("url", menuItem.getUrl());
        json.addProperty("folder", menuItem.getFolder());

        if (menuItem.getCreatedBy() != null) {
            json.addProperty("createdBy", menuItem.getCreatedBy().getUsername());
        }

        if (menuItem.getMenu() != null) {
            json.addProperty("menu", menuItem.getMenu().getExternalId());
        }

        JsonArray items = new JsonArray();
        menuItem.getChildrenSet().stream().forEach(item -> {
            JsonObject jItem = new JsonObject();
            jItem.addProperty("id", item.getExternalId());
            items.add(jItem);
        });
        json.add("menuItems", items);

        return json;

    }

}
