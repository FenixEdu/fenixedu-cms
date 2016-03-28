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
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.commons.i18n.LocalizedString;

import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.EDIT_MENU;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.LIST_MENUS;

@DefaultJsonAdapter(Menu.class)
public class MenuAdapter implements JsonAdapter<Menu> {

    @Override
    public Menu create(JsonElement arg0, JsonBuilder arg1) {
        // Depends on Site, implemented in {@SiteResource.createMenuFromJson}
        return null;
    }

    @Override
    public Menu update(JsonElement json, Menu menu, JsonBuilder ctx) {
        ensureCanDoThis(menu.getSite(), LIST_MENUS, EDIT_MENU);

        JsonObject jObj = json.getAsJsonObject();

        if (jObj.has("slug") && !jObj.get("slug").isJsonNull()) {
            menu.setSlug(jObj.get("slug").getAsString());
        }

        if (jObj.has("name") && !jObj.get("name").isJsonNull() && jObj.get("name").isJsonObject()) {
            menu.setName(LocalizedString.fromJson(jObj.get("name")));
        }

        Signal.emit(Menu.SIGNAL_EDITED, new DomainObjectEvent<>(menu));
        return menu;
    }

    @Override
    public JsonElement view(Menu menu, JsonBuilder ctx) {
        ensureCanDoThis(menu.getSite(), LIST_MENUS);

        JsonObject json = new JsonObject();

        json.addProperty("id", menu.getExternalId());
        json.addProperty("slug", menu.getSlug());
        json.addProperty("topMenu", menu.getTopMenu());

        json.add("creationDate", ctx.view(menu.getCreationDate(), DateTimeViewer.class));
        json.add("name", ctx.view(menu.getName(), LocalizedStringViewer.class));

        if (menu.getCreatedBy() != null) {
            json.addProperty("createdBy", menu.getCreatedBy().getUsername());
        }

        if (menu.getSite() != null) {
            json.addProperty("site", menu.getSite().getExternalId());
        }

        JsonArray items = new JsonArray();
        menu.getToplevelItemsSet().stream().forEach(item -> {
            JsonObject jItem = new JsonObject();
            jItem.addProperty("id", item.getExternalId());
            items.add(jItem);
        });
        json.add("menuItems", items);

        return json;

    }

}
