package org.fenixedu.cms.api.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.api.json.LocalizedStringViewer;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.cms.domain.CMSFolder;

@DefaultJsonAdapter(CMSFolder.class)
public class FolderAdapter implements JsonAdapter<CMSFolder> {

    @Override
    public CMSFolder create(JsonElement json, JsonBuilder ctx) {
        return null;
    }

    @Override
    public CMSFolder update(JsonElement arg0, CMSFolder arg1, JsonBuilder arg2) {
        return null;
    }

    @Override
    public JsonElement view(CMSFolder folder, JsonBuilder ctx) {
        JsonObject json = new JsonObject();

        json.addProperty("id", folder.getExternalId());

        if (folder.getFunctionality() != null) {
            json.add("description", ctx.view(folder.getFunctionality().getDescription(), LocalizedStringViewer.class));
            json.addProperty("path", folder.getFunctionality().getPath());
        }

        json.addProperty("custom", folder.getResolver() != null);

        return json;
    }

}
