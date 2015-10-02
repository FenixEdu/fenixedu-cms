package org.fenixedu.cms.api.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.api.json.DateTimeViewer;
import org.fenixedu.bennu.core.api.json.LocalizedStringViewer;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.cms.domain.PostContentRevision;
import org.fenixedu.cms.ui.AdminPosts;

@DefaultJsonAdapter(PostContentRevision.class)
public class PostRevisionAdapter implements JsonAdapter<PostContentRevision> {

    @Override
    public PostContentRevision create(JsonElement json, JsonBuilder ctx) {
        return null;
    }

    @Override
    public PostContentRevision update(JsonElement arg0, PostContentRevision arg1, JsonBuilder arg2) {
        return null;
    }

    @Override
    public JsonElement view(PostContentRevision revision, JsonBuilder ctx) {
        AdminPosts.ensureCanEditPost(revision.getPost());
        JsonObject json = new JsonObject();

        json.add("body", ctx.view(revision.getBody(), LocalizedStringViewer.class));

        if (revision.getCreatedBy() != null) {
            json.addProperty("createdBy", revision.getCreatedBy().getUsername());
        }

        if (revision.getNext() != null) {
            json.addProperty("next", revision.getNext().getExternalId());
        }
        if (revision.getPrevious() != null) {
            json.addProperty("previous", revision.getPrevious().getExternalId());
        }

        if (revision.getPost() != null) {
            json.addProperty("post", revision.getPost().getExternalId());
        }

        json.addProperty("id", revision.getExternalId());

        json.add("revisionDate", ctx.view(revision.getRevisionDate(), DateTimeViewer.class));

        return json;
    }

}
