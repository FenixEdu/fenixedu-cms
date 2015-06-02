package org.fenixedu.cms.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.adapters.DateTimeViewer;
import org.fenixedu.bennu.core.json.adapters.LocalizedStringViewer;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(Category.class)
public class CategoryAdapter implements JsonAdapter<Category> {

    @Override
    public Category create(JsonElement json, JsonBuilder ctx) {
        // Depends on Site, implemented in {@SiteResource.createCategoryFromJson}
        return null;
    }

    @Override
    public Category update(JsonElement json, Category category, JsonBuilder ctx) {
        JsonObject jObj = json.getAsJsonObject();

        if (jObj.has("name") && !jObj.get("name").isJsonNull()) {
            category.setName(LocalizedString.fromJson(jObj.get("name")));
        }

        if (jObj.has("slug") && !jObj.get("slug").isJsonNull()) {
            category.setSlug(jObj.get("slug").getAsString());
        }

        return category;
    }

    @Override
    public JsonElement view(Category category, JsonBuilder ctx) {
        JsonObject json = new JsonObject();

        json.addProperty("id", category.getExternalId());
        json.addProperty("slug", category.getSlug());

        json.add("creationDate", ctx.view(category.getCreationDate(), DateTimeViewer.class));
        json.add("name", ctx.view(category.getName(), LocalizedStringViewer.class));

        if (category.getCreatedBy() != null) {
            json.addProperty("createdBy", category.getCreatedBy().getUsername());
        }

        if (category.getSite() != null) {
            json.addProperty("site", category.getSite().getExternalId());
        }

        return json;
    }
}
