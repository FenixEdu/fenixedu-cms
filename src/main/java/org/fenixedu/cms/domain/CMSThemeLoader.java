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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CMSThemeLoader {

    final static Pattern RELATIVE_PARENT = Pattern.compile("^../|/../|/..$");
    final static Pattern RELATIVE_CURRENT = Pattern.compile("^./|/./|/.$");
    final static Pattern FULL_PATH = Pattern.compile("^/.*");
    public static Logger LOGGER = LoggerFactory.getLogger(CMSThemeLoader.class);

    private static List<EntryBean> getFolderEntries(ZipInputStream zin) {
        List<EntryBean> zipEntryBeans = Lists.newArrayList();
        ZipEntry zipEntry;
        try {
            while ((zipEntry = zin.getNextEntry()) != null) {
                zipEntryBeans.add(new ZipEntryBean(zin, zipEntry));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zipEntryBeans;
    }

    public static CMSTheme createFromZipStream(ZipInputStream zin) {
        return create(getFolderEntries(zin));
    }

    public static CMSTheme createFromZip(ZipFile zipFile) {
        return create(getZipEntries(zipFile));
    }

    public static CMSTheme createFromFolder(File folder) {
        return create(getFolderEntries(folder, folder));
    }

    private static List<EntryBean> getFolderEntries(File folder, File root) {
        List<EntryBean> folderChildren = Lists.newArrayList();
        for (File child : folder.listFiles()) {
            folderChildren.add(new FileEntryBean(child, root));
            if (child.isDirectory()) {
                folderChildren.addAll(getFolderEntries(child, root));
            }
        }
        return folderChildren;
    }

    private static List<EntryBean> getZipEntries(ZipFile zipFile) {
        List<EntryBean> zipEntryBeans = Lists.newArrayList();
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();

            zipEntryBeans.add(new ZipEntryBean(zipFile, zipEntry));
        }
        return zipEntryBeans;
    }

    private static CMSTheme create(List<EntryBean> entries) {
        JsonObject themeDescription = entries.stream().filter(entry -> entry.getName().endsWith("theme.json")).map(entry -> {
            return new JsonParser().parse(new String(entry.getContent())).getAsJsonObject();
        }).findAny().orElseThrow(() -> new IllegalArgumentException("Theme does not contain a theme.json file!"));

        CMSThemeFiles themeFiles =
                new CMSThemeFiles(loadFiles(entries.stream().filter(
                        entry -> (!entry.getName().equals("theme.json")) && validName(entry.getName()) && !entry.isDirectory())));

        return getOrCreateTheme(themeFiles, themeDescription);
    }

    private static boolean validName(String name) {
        return !(name.contains("__MACOSX") || name.contains("DS_Store") || RELATIVE_PARENT.matcher(name).matches()
                || RELATIVE_CURRENT.matcher(name).matches() || FULL_PATH.matcher(name).matches());
    }

    @Atomic(mode = TxMode.WRITE)
    private static CMSTheme getOrCreateTheme(CMSThemeFiles files, JsonObject themeDef) {
        String themeType = themeDef.get("type").getAsString();
        CMSTheme theme = CMSTheme.forType(themeType);
        if (theme != null) {
            return theme;
        }
        theme = new CMSTheme();
        theme.setDescription(themeDef.get("description").getAsString());
        theme.setName(themeDef.get("name").getAsString());
        theme.setBennu(Bennu.getInstance());
        theme.setType(themeType);

        HashSet<CMSTemplate> refused = Sets.newHashSet(theme.getTemplatesSet());

        loadExtends(themeDef, theme);
        loadPageTemplates(themeDef, theme, refused, files);

        for (CMSTemplate t : refused) {
            if (t.getPagesSet().size() != 0) {
                throw new RuntimeException("Cannot replace theme, '" + t.getType()
                        + "' is being used in some pages but is not included in new theme.");
            } else {
                t.delete();
            }
        }

        theme.setFiles(files);

        if (themeDef.has("thumbnail")) {
            CMSThemeFile thumbnail = theme.fileForPath(themeDef.get("thumbnail").getAsString());

            theme.setPreviewImage(new GroupBasedFile(thumbnail.getFileName(), thumbnail.getFullPath(), thumbnail.getContent(),
                    Group.anyone()));
            theme.setPreviewImagePath(themeDef.get("thumbnail").getAsString());
        }

        if (Bennu.getInstance().getCMSThemesSet().size() == 1) {
            Bennu.getInstance().setDefaultCMSTheme(theme);
        }

        return theme;
    }

    private static void loadExtends(JsonObject themeDef, CMSTheme theme) {
        if (themeDef.has("extends")) {
            String type = themeDef.get("extends").getAsString();
            if (type != null) {
                CMSTheme parent = CMSTheme.forType(type);
                if (parent == null) {
                    throw new RuntimeException("Extended theme does not exist");
                } else {
                    theme.setExtended(parent);
                }
            }
        }
    }

    private static void loadPageTemplates(JsonObject themeDef, CMSTheme theme, HashSet<CMSTemplate> refused, CMSThemeFiles files) {
        for (Entry<String, JsonElement> entry : themeDef.get("templates").getAsJsonObject().entrySet()) {
            String type = entry.getKey();
            JsonObject obj = entry.getValue().getAsJsonObject();

            CMSTemplate tp = Optional.ofNullable(theme.templateForType(type)).orElseGet(() -> new CMSTemplate());

            tp.setName(obj.get("name").getAsString());
            tp.setDescription(obj.get("description").getAsString());
            tp.setType(type);
            tp.setTheme(theme);

            if (refused.contains(tp)) {
                refused.remove(tp);
            }

            String path = obj.get("file").getAsString();
            CMSThemeFile file = files.getFileForPath(path);

            tp.setFilePath(path);

            if (file == null) {
                throw new RuntimeException("File in template '" + type + "' isn't in the Zip.");
            }
        }
    }

    private static Map<String, CMSThemeFile> loadFiles(Stream<EntryBean> files) {
        Map<String, CMSThemeFile> fileMap = new HashMap<>();
        files.forEach(bean -> {
            String filename = bean.getName();
            fileMap.put(bean.getName(),
                    new CMSThemeFile(Paths.get(filename).getFileName().toString(), filename, bean.getContent()));
        });
        return fileMap;
    }

    private static abstract class EntryBean {

        private final String name;
        private final boolean isDirectory;

        public EntryBean(String name, boolean isDirectory) {
            this.name = name;
            this.isDirectory = isDirectory;
        }

        public String getName() {
            return name;
        }

        public boolean isDirectory() {
            return isDirectory;
        }

        public abstract byte[] getContent();

    }

    private static class ZipEntryBean extends EntryBean {

        private final byte[] bytes;

        public ZipEntryBean(ZipFile zipFile, ZipEntry zipEntry) {
            super(zipEntry.getName(), zipEntry.isDirectory());
            try {
                this.bytes = ByteStreams.toByteArray(zipFile.getInputStream(zipEntry));
            } catch (IOException e) {
                throw new RuntimeException("Error reading the content of the zip file entry", e);
            }
        }

        public ZipEntryBean(ZipInputStream zin, ZipEntry zipEntry) {
            super(zipEntry.getName(), zipEntry.isDirectory());
            try {
                this.bytes = ByteStreams.toByteArray(zin);
            } catch (IOException e) {
                throw new RuntimeException("Error reading the content of the zip file entry", e);
            }
        }

        @Override
        public byte[] getContent() {
            return bytes;
        }
    }

    private static class FileEntryBean extends EntryBean {
        private final File file;

        public FileEntryBean(File file, File root) {
            super(root.toURI().relativize(file.toURI()).getPath(), file.isDirectory());
            this.file = file;
        }

        @Override
        public byte[] getContent() {
            try {
                return ByteStreams.toByteArray(new FileInputStream(file));
            } catch (IOException e) {
                throw new RuntimeException("Error reading the content of the zip file entry", e);
            }
        }
    }

}
