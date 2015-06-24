package org.fenixedu.cms.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.adapters.DateTimeViewer;
import org.fenixedu.bennu.core.json.adapters.LocalizedStringViewer;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(Menu.class)
public class MenuAdapter implements JsonAdapter<Menu> {

    @Override
    public Menu create(JsonElement arg0, JsonBuilder arg1) {
        // Depends on Site, implemented in {@SiteResource.createMenuFromJson}
        return null;
    }

    @Override
    public Menu update(JsonElement json, Menu menu, JsonBuilder ctx) {
        JsonObject jObj = json.getAsJsonObject();

        if (jObj.has("slug") && !jObj.get("slug").isJsonNull()) {
            menu.setSlug(jObj.get("slug").getAsString());
        }

        if (jObj.has("name") && !jObj.get("name").isJsonNull() && jObj.get("name").isJsonObject()) {
            menu.setName(LocalizedString.fromJson(jObj.get("name")));
        }

        return menu;
    }

    @Override
    public JsonElement view(Menu menu, JsonBuilder ctx) {
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
