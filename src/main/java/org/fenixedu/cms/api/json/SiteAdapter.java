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

import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.*;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.api.json.DateTimeViewer;
import org.fenixedu.bennu.core.api.json.LocalizedStringViewer;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.signals.DomainObjectEvent;
import org.fenixedu.bennu.core.signals.Signal;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;
import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.*;

@DefaultJsonAdapter(Site.class)
public class SiteAdapter implements JsonAdapter<Site> {

    @Override
    public Site create(JsonElement json, JsonBuilder ctx) {
        JsonObject jObj = json.getAsJsonObject();

        JsonElement name = getRequiredValue(jObj, "name");
        JsonElement description = getRequiredValue(jObj, "description");

        Site site = new Site(LocalizedString.fromJson(name), LocalizedString.fromJson(description));

        if (jObj.has("theme") && !jObj.get("theme").isJsonNull()) {
            site.setTheme(FenixFramework.getDomainObject(jObj.get("theme").getAsString()));
        }

        if (jObj.has("embedded") && !jObj.get("embedded").isJsonNull()) {
            site.setEmbedded(jObj.get("embedded").getAsBoolean());
        }
        
        site.updateMenuFunctionality();

        return site;
    }

    @Override
    public Site update(JsonElement json, Site site, JsonBuilder ctx) {
        JsonObject jObj = json.getAsJsonObject();

        if (jObj.has("slug") && !jObj.get("slug").isJsonNull()) {
            ensureCanDoThis(site, CHOOSE_PATH_AND_FOLDER);
            site.isValidSlug(jObj.get("slug").getAsString());
            site.setSlug(jObj.get("slug").getAsString());
        }

        if (jObj.has("name") && !jObj.get("name").isJsonNull() && jObj.get("name").isJsonObject()) {
            ensureCanDoThis(site, EDIT_SITE_INFORMATION);
            site.setName(LocalizedString.fromJson(jObj.get("name")));
        }

        if (jObj.has("description") && !jObj.get("description").isJsonNull() && jObj.get("description").isJsonObject()) {
            ensureCanDoThis(site, EDIT_SITE_INFORMATION);
            site.setDescription(LocalizedString.fromJson(jObj.get("description")));
        }

        if (jObj.has("analyticsCode") && !jObj.get("analyticsCode").isJsonNull()) {
            ensureCanDoThis(site, MANAGE_ANALYTICS);
            site.setAnalyticsCode(jObj.get("analyticsCode").getAsString());
        }

        if (jObj.has("folder") && !jObj.get("folder").isJsonNull()){
            ensureCanDoThis(site,CHOOSE_PATH_AND_FOLDER);
            site.setFolder(FenixFramework.getDomainObject(jObj.get("folder").getAsString()));
        }

        if (jObj.has("theme") && !jObj.get("theme").isJsonNull()) {
            ensureCanDoThis(site, CHANGE_THEME);
            site.setTheme(FenixFramework.getDomainObject(jObj.get("theme").getAsString()));
        }

        if (jObj.has("alternativeSite") && !jObj.get("alternativeSite").isJsonNull()) {
            ensureCanDoThis(site, EDIT_SITE_INFORMATION);
            site.setAlternativeSite(jObj.get("alternativeSite").getAsString());
        }

        if (jObj.has("published") && !jObj.get("published").isJsonNull()) {
            ensureCanDoThis(site, PUBLISH_SITE);
            site.setPublished(jObj.get("published").getAsBoolean());
        }

        Signal.emit(Site.SIGNAL_EDITED, new DomainObjectEvent<>(site));
        return site;
    }

    @Override
    public JsonElement view(Site site, JsonBuilder ctx) {
        JsonObject json = new JsonObject();

        json.addProperty("id", site.getExternalId());
        json.addProperty("slug", site.getSlug());
        json.add("name", ctx.view(site.getName(), LocalizedStringViewer.class));
        json.add("description", ctx.view(site.getDescription(), LocalizedStringViewer.class));
        json.addProperty("alternativeSite", site.getAlternativeSite());
        json.add("creationDate", ctx.view(site.getCreationDate(), DateTimeViewer.class));
        json.addProperty("published", site.getPublished());
        json.addProperty("embedded", site.getEmbedded());
        json.addProperty("analyticsCode", site.getAnalyticsCode());

        if (site.getTheme() != null) {
            json.addProperty("theme", site.getTheme().getExternalId());
        }

        if (site.getCreatedBy() != null) {
            json.addProperty("createdBy", site.getCreatedBy().getUsername());
        }

        return json;
    }

    protected JsonElement getRequiredValue(JsonObject obj, String property) {
        if (obj.has(property)) {
            return obj.get(property);
        }
        throw BennuCoreDomainException.cannotCreateEntity();
    }
}
