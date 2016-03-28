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
import org.fenixedu.bennu.core.api.json.LocalizedStringViewer;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.signals.DomainObjectEvent;
import org.fenixedu.bennu.core.signals.Signal;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.ui.AdminPosts;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

@DefaultJsonAdapter(Post.class)
public class PostAdapter implements JsonAdapter<Post> {

    @Override
    public Post create(JsonElement json, JsonBuilder ctx) {
        // Depends on Site, implemented in {@SiteResource#createPostFromJson}
        return null;
    }

    @Override
    public Post update(JsonElement json, Post post, JsonBuilder ctx) {
        AdminPosts.ensureCanEditPost(post);
        JsonObject jObj = json.getAsJsonObject();

        if (jObj.has("name") && !jObj.get("name").isJsonNull() && jObj.get("name").isJsonObject()) {
            LocalizedString name = LocalizedString.fromJson(jObj.get("name"));
            if (!post.getName().equals(name)) {
                post.setName(name);
            }
        }

        if (jObj.has("slug") && !jObj.get("slug").isJsonNull()) {
            String slug = jObj.get("slug").getAsString();
            if (!post.getSlug().equals(slug)) {
                post.setSlug(slug);
            }
        }

        if (jObj.has("body") && !jObj.get("body").isJsonNull() && jObj.get("body").isJsonObject()) {
            LocalizedString body = LocalizedString.fromJson(jObj.get("body"));
            LocalizedString excerpt = null;
            if(jObj.has("excerpt") && !jObj.get("excerpt").isJsonNull() && jObj.get("excerpt").isJsonObject()) {
                excerpt = LocalizedString.fromJson(jObj.get("excerpt"));
            }
            if (!post.getBody().equals(body) || !post.getExcerpt().equals(excerpt)) {
                post.setBodyAndExcerpt(body,excerpt);
            }
        }

        if (jObj.has("published") && !jObj.get("published").isJsonNull()) {
            boolean published = jObj.get("published").getAsBoolean();
            if (post.getActive() != published) {
                post.setActive(published);
            }
        }

        if (jObj.has("publicationBegin") && !jObj.get("publicationBegin").isJsonNull()) {
            String date = jObj.get("publicationBegin").getAsString();
            DateTime dateTime = parseDate(date);
            if (!dateTime.isEqual(post.getPublicationBegin())) {
                post.setPublicationBegin(dateTime);
            }
        }

        if (jObj.has("publicationEnd") && !jObj.get("publicationEnd").isJsonNull()) {
            String date = jObj.get("publicationEnd").getAsString();
            DateTime dateTime = parseDate(date);
            if (!dateTime.isEqual(post.getPublicationEnd())) {
                post.setPublicationEnd(dateTime);
            }
        }

        Signal.emit(Post.SIGNAL_EDITED, new DomainObjectEvent<>(post));
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
