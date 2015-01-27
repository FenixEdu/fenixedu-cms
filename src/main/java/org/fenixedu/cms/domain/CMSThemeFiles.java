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
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class CMSThemeFiles {

    private final Map<String, CMSThemeFile> files;
    private final String checksum;

    public CMSThemeFiles(Map<String, CMSThemeFile> files) {
        this.files = ImmutableMap.copyOf(files);
        this.checksum = computeChecksum();
    }

    public CMSThemeFiles(byte[] bytes) {
        this.files = getMapFromStream(bytes);
        this.checksum = computeChecksum();
    }

    private String computeChecksum() {
        StringBuilder builder = new StringBuilder();
        this.files.values().forEach(file -> {
            builder.append(Hashing.sha256().hashBytes(file.getContent()).toString());
        });
        return Hashing.murmur3_128().hashString(builder, StandardCharsets.UTF_8).toString().substring(0, 16);
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

    public boolean checksumMatches(CMSThemeFiles files) {
        return this.checksum.equals(files.checksum);
    }

    public String getChecksum() {
        return this.checksum;
    }
}
