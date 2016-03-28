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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.tika.Tika;
import org.joda.time.DateTime;

import java.util.Base64;

public class CMSThemeFile {

    private static final Tika tika = new Tika();

    private final String fullPath;
    private final byte[] content;
    private final String contentType;
    private final String fileName;
    private final DateTime lastModified;

    public CMSThemeFile(String filename, String fullPath, byte[] content) {
        this.fileName = filename;
        this.fullPath = fullPath;
        this.content = content;
        this.contentType = tika.detect(content, filename);
        this.lastModified = DateTime.now();
    }

    CMSThemeFile(JsonObject json) {
        this.fileName = json.get("fileName").getAsString();
        this.fullPath = json.get("fullPath").getAsString();
        this.contentType = json.get("contentType").getAsString();
        this.content = Base64.getDecoder().decode(json.get("content").getAsString());
        this.lastModified = new DateTime(json.get("lastModified").getAsLong());
    }

    public String getFullPath() {
        return fullPath;
    }

    public byte[] getContent() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return content.length;
    }

    public DateTime getLastModified() {
        return lastModified;
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("fileName", fileName);
        json.addProperty("fullPath", fullPath);
        json.addProperty("contentType", contentType);
        json.addProperty("content", Base64.getEncoder().encodeToString(content));
        json.addProperty("lastModified", lastModified.getMillis());
        return json;
    }
}
