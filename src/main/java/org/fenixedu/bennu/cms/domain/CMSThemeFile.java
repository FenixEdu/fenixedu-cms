package org.fenixedu.bennu.cms.domain;

import java.util.Base64;

import org.apache.tika.Tika;
import org.joda.time.DateTime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
