package org.fenixedu.cms.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.io.servlets.FileDownloadServlet;
import org.fenixedu.cms.domain.PostFile;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(PostFile.class)
public class PostFileAdapter implements JsonAdapter<PostFile> {

    @Override
    public PostFile create(JsonElement json, JsonBuilder ctx) {
        // Depends on Post, implemented in {@PostResource.createFileFromRequest}
        return null;
    }

    @Override
    public PostFile update(JsonElement json, PostFile postFile, JsonBuilder ctx) {
        JsonObject jObj = json.getAsJsonObject();

        if (jObj.has("index") && !jObj.get("index").isJsonNull()) {
            postFile.setIndex(jObj.get("index").getAsInt());
        }

        if (jObj.has("isEmbedded") && !jObj.get("isEmbedded").isJsonNull()) {
            postFile.setIsEmbedded(jObj.get("isEmbedded").getAsBoolean());
        }

        if (jObj.has("accessGroup") && !jObj.get("accessGroup").isJsonNull()) {
            postFile.getFiles().setAccessGroup(Group.parse(jObj.get("accessGroup").getAsString()));
        }

        return postFile;
    }

    @Override
    public JsonElement view(PostFile postFile, JsonBuilder ctx) {
        JsonObject json = new JsonObject();

        json.addProperty("id", postFile.getExternalId());
        json.addProperty("index", postFile.getIndex());
        json.addProperty("isEmbedded", postFile.getIsEmbedded());
        json.addProperty("post", postFile.getPost().getExternalId());

        GroupBasedFile file = postFile.getFiles();
        json.addProperty("checksum", file.getChecksum());
        json.addProperty("contentType", file.getContentType());
        json.addProperty("displayName", file.getDisplayName());
        json.addProperty("filename", file.getFilename());
        json.addProperty("size", file.getSize());

        if (file.getAccessGroup() != null) {
            json.addProperty("accessGroup", file.getAccessGroup().getPresentationName());
        }

        json.addProperty("url", FileDownloadServlet.getDownloadUrl(file));

        //TODO missing fields

        return json;
    }

}
