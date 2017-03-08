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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.api.json.DateTimeViewer;
import org.fenixedu.bennu.core.api.json.LocalizedStringViewer;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.signals.DomainObjectEvent;
import org.fenixedu.bennu.core.signals.Signal;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.commons.i18n.LocalizedString;

import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.EDIT_CATEGORY;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.LIST_CATEGORIES;

@DefaultJsonAdapter(Category.class)
public class CategoryAdapter implements JsonAdapter<Category> {

    @Override
    public Category create(JsonElement json, JsonBuilder ctx) {
        // Depends on Site, implemented in {@SiteResource.createCategoryFromJson}
        return null;
    }

    @Override
    public Category update(JsonElement json, Category category, JsonBuilder ctx) {
        ensureCanDoThis(category.getSite(), LIST_CATEGORIES, EDIT_CATEGORY);

        JsonObject jObj = json.getAsJsonObject();

        if (jObj.has("name") && !jObj.get("name").isJsonNull() && jObj.get("name").isJsonObject()) {
            category.setName(LocalizedString.fromJson(jObj.get("name")));
        }

        if (jObj.has("slug") && !jObj.get("slug").isJsonNull()) {
            category.setSlug(jObj.get("slug").getAsString());
        }

        Signal.emit(Category.SIGNAL_EDITED, new DomainObjectEvent<>(category));
        return category;
    }

    @Override
    public JsonElement view(Category category, JsonBuilder ctx) {
        ensureCanDoThis(category.getSite(), LIST_CATEGORIES);

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
