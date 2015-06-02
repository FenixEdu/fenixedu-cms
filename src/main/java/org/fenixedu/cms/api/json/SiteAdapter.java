package org.fenixedu.cms.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.adapters.DateTimeViewer;
import org.fenixedu.bennu.core.json.adapters.LocalizedStringViewer;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(Site.class)
public class SiteAdapter implements JsonAdapter<Site> {

    @Override
    public Site create(JsonElement json, JsonBuilder ctx) {

        JsonObject jObj = json.getAsJsonObject();

        //TODO: getRequired
        Site site = new Site(LocalizedString.fromJson(jObj.get("name")), LocalizedString.fromJson(jObj.get("description")));

        if (jObj.has("theme") && !jObj.get("theme").isJsonNull()) {
            site.setTheme(FenixFramework.getDomainObject(jObj.get("theme").getAsString()));
        }

        if (jObj.has("embedded") && !jObj.get("embedded").isJsonNull()) {
            site.setEmbedded(jObj.get("embedded").getAsBoolean());
        }

        if (jObj.has("template") && !jObj.get("template").isJsonNull()) {
            String template = jObj.get("template").getAsString();
            Site.templateFor(template).makeIt(site);
        }

        //FIXME: padrada (x2) para funcionar
        site.updateMenuFunctionality();
        site.setPublished(false);

        return site;
    }

    @Override
    public Site update(JsonElement json, Site site, JsonBuilder ctx) {

        JsonObject jObj = json.getAsJsonObject();

        if (jObj.has("slug") && !jObj.get("slug").isJsonNull()) {
            site.setSlug(jObj.get("slug").getAsString());
        }

        if (jObj.has("name") && !jObj.get("name").isJsonNull()) {
            site.setName(LocalizedString.fromJson(jObj.get("name")));
        }

        if (jObj.has("description") && !jObj.get("description").isJsonNull()) {
            site.setDescription(LocalizedString.fromJson(jObj.get("description")));
        }

        if (jObj.has("analyticsCode") && !jObj.get("analyticsCode").isJsonNull()) {
            site.setAnalyticsCode(jObj.get("analyticsCode").getAsString());
        }

        if (jObj.has("theme") && !jObj.get("theme").isJsonNull()) {
            site.setTheme(FenixFramework.getDomainObject(jObj.get("theme").getAsString()));
        }

        if (jObj.has("alternativeSite") && !jObj.get("alternativeSite").isJsonNull()) {
            site.setAlternativeSite(jObj.get("alternativeSite").getAsString());
        }

        if (jObj.has("published") && !jObj.get("published").isJsonNull()) {
            site.setPublished(jObj.get("published").getAsBoolean());
        }

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

    protected String getRequiredValue(JsonObject obj, String property) {
        if (obj.has(property)) {
            return obj.get(property).getAsString();
        }
        throw BennuCoreDomainException.cannotCreateEntity();
    }
}
