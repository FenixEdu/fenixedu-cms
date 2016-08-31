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
package org.fenixedu.cms.domain;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.fenixedu.cms.domain.wraps.Wrap;
import org.fenixedu.cms.domain.wraps.Wrappable;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PostMetadata implements Serializable, Wrappable {

    private static final long serialVersionUID = 4890885803531605616L;

    private final JsonObject metadata;

    public PostMetadata() {
        this(new JsonObject());
    }

    public PostMetadata(JsonObject metadata) {
        this.metadata = metadata;
    }

    public PostMetadata with(String key, LocalizedString value) {
        if (key != null && value != null) {
            metadata.add(key, value.json());
        }
        return new PostMetadata(metadata);
    }

    public PostMetadata with(String key, DateTime value) {
        if (key != null && value != null) {
            metadata.addProperty(key, value.toDateTimeISO().toString());
        }
        return new PostMetadata(metadata);
    }

    public PostMetadata with(String key, String value) {
        metadata.addProperty(key, value);
        return new PostMetadata(metadata);
    }

    public PostMetadata with(String key, Number value) {
        metadata.addProperty(key, value);
        return new PostMetadata(metadata);
    }

    public PostMetadata with(String key, Boolean value) {
        metadata.addProperty(key, value);
        return new PostMetadata(metadata);
    }

    public PostMetadata without(String key) {
        metadata.remove(key);
        return new PostMetadata(metadata);
    }

    public Optional<LocalizedString> getAsLocalizedString(String key) {
        return contains(key) ? Optional.ofNullable(LocalizedString.fromJson(get(key).get())) : Optional.empty();
    }

    public Optional<DateTime> getAsDateTime(String key) {
        return contains(key) ? Optional.ofNullable(DateTime.parse(get(key).get().getAsString())) : Optional.empty();
    }

    public Optional<String> getAsString(String key) {
        return contains(key) ? Optional.ofNullable(get(key).get().getAsString()) : Optional.empty();
    }

    public Optional<Number> getAsNumber(String key) {
        return contains(key) ? Optional.ofNullable(get(key).get().getAsNumber()) : Optional.empty();
    }

    public Optional<Boolean> getAsBoolean(String key) {
        return contains(key) ? Optional.ofNullable(get(key).get().getAsBoolean()) : Optional.empty();
    }

    public Optional<JsonElement> get(String key) {
        return contains(key) ? Optional.ofNullable(metadata.get(key)) : Optional.empty();
    }

    public Set<Map.Entry<String, JsonElement>> getEntries() {
        return metadata.entrySet();
    }

    public boolean contains(String key) {
        return metadata.get(key) != null && !metadata.get(key).isJsonNull();
    }

    public static PostMetadata fromJson(JsonElement json) {
        return new PostMetadata(json.getAsJsonObject());
    }

    public String externalize() {
        return metadata.toString();
    }

    public JsonElement json() {
        return metadata;
    }

    public static PostMetadata internalize(String json) {
        return new PostMetadata(new Gson().fromJson(json, JsonElement.class).getAsJsonObject());
    }

    public class PostMetadataWrap extends Wrap {
        public JsonElement get(String key) {
            return PostMetadata.this.metadata.get(key);
        }

        public Set<Map.Entry<String, JsonElement>> getEntries() {
            return PostMetadata.this.getEntries();
        }
    }

    @Override
    public Wrap makeWrap() {
        return new PostMetadataWrap();
    }

    @Override
    public String toString() {
        return externalize();
    }

    @Override
    public PostMetadata clone() {
        return new PostMetadata(metadata);
    }

    @Override
    public boolean equals(Object object) {
        return PostMetadata.class.isInstance(object) && ((PostMetadata) object).json().equals(json());
    }
}
