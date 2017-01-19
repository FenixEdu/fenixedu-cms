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
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.cms.domain.CmsSettings;
import org.fenixedu.cms.domain.SiteBuilder;
import pt.ist.fenixframework.FenixFramework;

@DefaultJsonAdapter(SiteBuilder.class)
public class SiteBuilderAdapter implements JsonAdapter<SiteBuilder> {

    @Override
    public SiteBuilder create(JsonElement json, JsonBuilder ctx) {
        JsonObject jObj = json.getAsJsonObject();

        String slug = getRequiredValue(jObj, "slug").getAsString();
    
        SiteBuilder builder = new SiteBuilder(slug);
        
        if (jObj.has("theme") && !jObj.get("theme").isJsonNull()) {
            builder.setTheme(FenixFramework.getDomainObject(jObj.get("theme").getAsString()));
        }
    
        if (jObj.has("folder") && !jObj.get("folder").isJsonNull()) {
            builder.setFolder(FenixFramework.getDomainObject(jObj.get("folder").getAsString()));
        }

        if (jObj.has("embedded") && !jObj.get("embedded").isJsonNull()) {
            builder.setEmbedded(jObj.get("embedded").getAsBoolean());
        }
    
        if (jObj.has("roles") && !jObj.get("roles").isJsonArray()) {
            jObj.get("embedded").getAsJsonArray().forEach(role->
                builder.addRoleTemplate(FenixFramework.getDomainObject(role.getAsString()))
            );
        }
        return builder;
    }

    @Override
    public SiteBuilder update(JsonElement json, SiteBuilder builder, JsonBuilder ctx) {
        JsonObject jObj = json.getAsJsonObject();
        CmsSettings.getInstance().ensureCanManageSettings();
    
    
    
        if (jObj.has("theme") && !jObj.get("theme").isJsonNull()) {
            builder.setTheme(FenixFramework.getDomainObject(jObj.get("theme").getAsString()));
        }
    
        if (jObj.has("folder") && !jObj.get("folder").isJsonNull()) {
            builder.setFolder(FenixFramework.getDomainObject(jObj.get("folder").getAsString()));
        }
    
        if (jObj.has("embedded") && !jObj.get("embedded").isJsonNull()) {
            builder.setEmbedded(jObj.get("embedded").getAsBoolean());
        }
    
        if (jObj.has("roles") && !jObj.get("roles").isJsonArray()) {
            jObj.get("embedded").getAsJsonArray().forEach(role->
                builder.addRoleTemplate(FenixFramework.getDomainObject(role.getAsString()))
            );
        }

        return builder;
    }

    @Override
    public JsonElement view(SiteBuilder builder, JsonBuilder ctx) {
        JsonObject json = new JsonObject();
        json.addProperty("slug", builder.getSlug());
        json.addProperty("published", builder.getPublished());
        json.addProperty("embedded", builder.getEmbedded());

        if (builder.getTheme() != null) {
            json.addProperty("theme", builder.getTheme().getExternalId());
        }

        if (builder.getFolder() != null) {
            json.addProperty("createdBy", builder.getFolder().getExternalId());
        }
        
        json.add("roles", ctx.view(builder.getRoleTemplateSet()));

        return json;
    }

    private JsonElement getRequiredValue(JsonObject obj, String property) {
        if (obj.has(property)) {
            return obj.get(property);
        }
        throw BennuCoreDomainException.cannotCreateEntity();
    }
}
