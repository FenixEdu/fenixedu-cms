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
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.cms.domain.CMSTheme;

@DefaultJsonAdapter(CMSTheme.class)
public class ThemeAdapter implements JsonAdapter<CMSTheme> {

    @Override
    public CMSTheme create(JsonElement arg0, JsonBuilder arg1) {
        return null;
    }

    @Override
    public CMSTheme update(JsonElement arg0, CMSTheme arg1, JsonBuilder arg2) {
        return null;
    }

    @Override
    public JsonElement view(CMSTheme theme, JsonBuilder ctx) {
        JsonObject json = new JsonObject();

        if (theme.getCreatedBy() != null) {
            json.addProperty("createdBy", theme.getCreatedBy().getUsername());
        }
        json.add("creationDate", ctx.view(theme.getCreationDate(), DateTimeViewer.class));
        json.addProperty("name", theme.getName());
        json.addProperty("description", theme.getDescription());
        json.addProperty("type", theme.getType());
        json.addProperty("id", theme.getExternalId());

        return json;
    }

}
