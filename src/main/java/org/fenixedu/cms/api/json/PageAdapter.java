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
import org.fenixedu.cms.domain.Page;
import org.fenixedu.commons.i18n.LocalizedString;

import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.EDIT_PAGE;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.SEE_PAGES;

@DefaultJsonAdapter(Page.class)
public class PageAdapter implements JsonAdapter<Page> {

    @Override
    public Page create(JsonElement json, JsonBuilder ctx) {
        // Depends on Site, implemented in {@SiteResource.createPageFromJson}
        return null;
    }

    @Override
    public Page update(JsonElement json, Page page, JsonBuilder ctx) {
        ensureCanDoThis(page.getSite(), SEE_PAGES, EDIT_PAGE);
        JsonObject jObj = json.getAsJsonObject();

        if (jObj.has("name") && !jObj.get("name").isJsonNull() && jObj.get("name").isJsonObject()) {
            page.setName(LocalizedString.fromJson(jObj.get("name")));
        }

        if (jObj.has("slug") && !jObj.get("slug").isJsonNull()) {
            page.setSlug(jObj.get("slug").getAsString());
        }

        if (jObj.has("published") && !jObj.get("published").isJsonNull()) {
            page.setPublished(jObj.get("published").getAsBoolean());
        }

        Signal.emit(Page.SIGNAL_EDITED, new DomainObjectEvent<>(page));
        return page;
    }

    @Override
    public JsonElement view(Page page, JsonBuilder ctx) {
        ensureCanDoThis(page.getSite(), SEE_PAGES);
        JsonObject json = new JsonObject();

        json.addProperty("id", page.getExternalId());
        json.add("creationDate", ctx.view(page.getCreationDate(), DateTimeViewer.class));
        json.add("modificationDate", ctx.view(page.getModificationDate(), DateTimeViewer.class));
        json.addProperty("published", page.getPublished());
        json.addProperty("site", page.getSite().getExternalId());

        if (page.getCreatedBy() != null) {
            json.addProperty("createdBy", page.getCreatedBy().getUsername());
        }

        if (page.getName() != null) {
            json.add("name", ctx.view(page.getName(), LocalizedStringViewer.class));
        }

        if (page.getSlug() != null) {
            json.addProperty("slug", page.getSlug());
        }

        return json;
    }

}
