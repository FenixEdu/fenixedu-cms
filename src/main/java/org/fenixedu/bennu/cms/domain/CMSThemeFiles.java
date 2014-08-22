package org.fenixedu.bennu.cms.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class CMSThemeFiles {

    private final Map<String, CMSThemeFile> files;

    public CMSThemeFiles(Map<String, CMSThemeFile> files) {
        this.files = ImmutableMap.copyOf(files);
    }

    public CMSThemeFiles(byte[] bytes) {
        this.files = getMapFromStream(bytes);
    }

    public CMSThemeFile getFileForPath(String path) {
        return files.get(path);
    }

    public Collection<CMSThemeFile> getFiles() {
        return files.values().stream().sorted(Comparator.comparing(CMSThemeFile::getFileName)).collect(Collectors.toList());
    }

    public JsonArray toJson() {
        JsonArray array = new JsonArray();
        for (CMSThemeFile file : files.values()) {
            array.add(file.toJson());
        }
        return array;
    }

    public byte[] externalize() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream stream = new GZIPOutputStream(out);
            stream.write(toJson().toString().getBytes(StandardCharsets.UTF_8));
            stream.close();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Could not externalize CMSThemeFiles!", e);
        }
    }

    private Map<String, CMSThemeFile> getMapFromStream(byte[] bytes) {
        try {
            GZIPInputStream stream = new GZIPInputStream(new ByteArrayInputStream(bytes));
            byte[] unzipped = ByteStreams.toByteArray(stream);
            String json = new String(unzipped, StandardCharsets.UTF_8);
            JsonArray array = new JsonParser().parse(json).getAsJsonArray();
            ImmutableMap.Builder<String, CMSThemeFile> files = ImmutableMap.builder();
            for (JsonElement file : array) {
                CMSThemeFile themeFile = new CMSThemeFile(file.getAsJsonObject());
                files.put(themeFile.getFullPath(), themeFile);
            }
            return files.build();
        } catch (IOException e) {
            throw new RuntimeException("Could not internalize CMSThemeFiles!", e);
        }
    }

    public CMSThemeFiles with(CMSThemeFile file) {
        Map<String, CMSThemeFile> files = new HashMap<String, CMSThemeFile>(this.files);
        files.put(file.getFullPath(), file);
        return new CMSThemeFiles(files);
    }

    public CMSThemeFiles without(String path) {
        Map<String, CMSThemeFile> files = new HashMap<String, CMSThemeFile>(this.files);
        files.remove(path);
        return new CMSThemeFiles(files);
    }

    public long getTotalSize() {
        return files.values().stream().mapToLong(CMSThemeFile::getFileSize).sum();
    }
}
