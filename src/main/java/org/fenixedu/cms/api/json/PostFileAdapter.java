package org.fenixedu.cms.api.json;

import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.EDIT_PAGE;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.EDIT_POSTS;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.SEE_PAGES;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.io.servlets.FileDownloadServlet;
import org.fenixedu.bennu.signals.DomainObjectEvent;
import org.fenixedu.bennu.signals.Signal;
import org.fenixedu.cms.domain.PostFile;
import org.fenixedu.cms.exceptions.CmsDomainException;

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
        if(postFile.getPost()!=null && postFile.getPost().isStaticPost()) {
            ensureCanDoThis(postFile.getSite(), SEE_PAGES, EDIT_PAGE);
        } else {
            ensureCanDoThis(postFile.getSite(), EDIT_POSTS);
        }
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

        Signal.emit(PostFile.SIGNAL_EDITED, new DomainObjectEvent<>(postFile));
        return postFile;
    }

    @Override
    public JsonElement view(PostFile postFile, JsonBuilder ctx) {
        if(postFile.getPost()!=null && postFile.getPost().isStaticPost()) {
            ensureCanDoThis(postFile.getSite(), SEE_PAGES);
        }

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
            if(!file.getAccessGroup().isMember(Authenticate.getUser())) {
                throw CmsDomainException.forbiden();
            }
            json.addProperty("accessGroup", file.getAccessGroup().getPresentationName());
        }

        json.addProperty("url", FileDownloadServlet.getDownloadUrl(file));

        return json;
    }

}
