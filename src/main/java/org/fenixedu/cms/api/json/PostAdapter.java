package org.fenixedu.cms.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.adapters.DateTimeViewer;
import org.fenixedu.bennu.core.json.adapters.LocalizedStringViewer;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(Post.class)
public class PostAdapter implements JsonAdapter<Post> {

    @Override
    public Post create(JsonElement json, JsonBuilder ctx) {
        // Depends on Site, implemented in {@SiteResource#createPostFromJson}
        return null;
    }

    @Override
    public Post update(JsonElement json, Post post, JsonBuilder ctx) {
        //TODO ensure idempotent

        JsonObject jObj = json.getAsJsonObject();

        if (jObj.has("name") && !jObj.get("name").isJsonNull()) {
            post.setName(LocalizedString.fromJson(jObj.get("name")));
        }

        if (jObj.has("slug") && !jObj.get("slug").isJsonNull()) {
            post.setSlug(jObj.get("slug").getAsString());
        }

        if (jObj.has("body") && !jObj.get("body").isJsonNull()) {
            post.setBody(LocalizedString.fromJson(jObj.get("body")));
        }

        if (jObj.has("published") && !jObj.get("published").isJsonNull()) {
            post.setActive(jObj.get("published").getAsBoolean());
        }

        if (jObj.has("publicationBegin") && !jObj.get("publicationBegin").isJsonNull()) {
            String date = jObj.get("publicationBegin").getAsString();
            DateTime dateTime = parseDate(date);
            post.setPublicationBegin(dateTime);
        }

        if (jObj.has("publicationEnd") && !jObj.get("publicationEnd").isJsonNull()) {
            String date = jObj.get("publicationEnd").getAsString();
            DateTime dateTime = parseDate(date);
            post.setPublicationEnd(dateTime);
        }

        return post;
    }

    public static DateTime parseDate(String date) {
        DateTimeFormatter formatter = ISODateTimeFormat.dateHourMinute();
        DateTime dateTime = formatter.parseDateTime(date);
        return dateTime;
    }

    @Override
    public JsonElement view(Post post, JsonBuilder ctx) {
        JsonObject json = new JsonObject();

        json.addProperty("id", post.getExternalId());
        json.add("name", ctx.view(post.getName(), LocalizedStringViewer.class));
        json.add("body", ctx.view(post.getBody(), LocalizedStringViewer.class));
        json.add("creationDate", ctx.view(post.getCreationDate(), DateTimeViewer.class));
        json.add("modificationDate", ctx.view(post.getModificationDate(), DateTimeViewer.class));
        json.add("publicationBegin", ctx.view(post.getPublicationBegin(), DateTimeViewer.class));
        json.add("publicationEnd", ctx.view(post.getPublicationEnd(), DateTimeViewer.class));

        if (post.getCreatedBy() != null) {
            json.addProperty("createdBy", post.getCreatedBy().getUsername());
        }

        if (post.getSite() != null) {
            json.addProperty("site", post.getSite().getExternalId());
        }

        json.addProperty("slug", post.getSlug());
        json.addProperty("published", post.getActive());

        return json;
    }

}
