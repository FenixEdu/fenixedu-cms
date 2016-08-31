package org.fenixedu.cms.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.adapters.DateTimeViewer;
import org.fenixedu.cms.domain.CMSTheme;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
