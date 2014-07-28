package org.fenixedu.bennu.cms.domain;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.fenixedu.bennu.core.domain.Bennu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CMSThemeLoader {

    final static Pattern RELATIVE_PARENT = Pattern.compile("^../|/../|/..$");
    final static Pattern RELATIVE_CURRENT = Pattern.compile("^./|/./|/.$");
    final static Pattern FULL_PATH = Pattern.compile("^/.*");
    public static Logger LOGGER = LoggerFactory.getLogger(CMSThemeLoader.class);

    private static List<ZipEntryBean> getFolderEntries(ZipInputStream zin) {
        List<ZipEntryBean> zipEntryBeans = Lists.newArrayList();
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
        return create(getFolderEntries(folder));
    }

    private static List<FileEntryBean> getFolderEntries(File folder) {
        List<FileEntryBean> folderChildren = Lists.newArrayList();
        for (File child : folder.listFiles()) {
            folderChildren.add(new FileEntryBean(child));
            if (child.isDirectory()) {
                folderChildren.addAll(getFolderEntries(child));
            }
        }
        return folderChildren;
    }

    private static List<ZipEntryBean> getZipEntries(ZipFile zipFile) {
        List<ZipEntryBean> zipEntryBeans = Lists.newArrayList();
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            zipEntryBeans.add(new ZipEntryBean(zipFile, zipEntry));
        }
        return zipEntryBeans;
    }

    private static CMSTheme create(List<? extends EntryBean> entries) {
        String prefix = null;
        boolean isOnlyDirectory = true;

        HashMap<String, ByteArrayOutputStream> files = new HashMap<>();

        JsonObject themeDescription = null;

        for (EntryBean entry : entries) {
            if (entry.getName().contains("__MACOSX")) {
                continue;
            }

            if (entry.getName().contains("DS_Store")) {
                continue;
            }

            if (entry.isDirectory()) {
                continue;
            }

            if (RELATIVE_PARENT.matcher(entry.getName()).matches() || RELATIVE_CURRENT.matcher(entry.getName()).matches()
                    || FULL_PATH.matcher(entry.getName()).matches()) {
                continue;
            }

            if (prefix == null) {
                prefix = entry.getName().split("/")[0];
            }

            if (!entry.getName().startsWith(prefix)) {
                isOnlyDirectory = false;
            }

            if (entry.getName().endsWith("theme.json")) {
                byte[] entryContent = entry.getArrayOutputStream().toByteArray();
                themeDescription = new JsonParser().parse(new String(entryContent)).getAsJsonObject();
            } else {
                files.put(entry.getName(), entry.getArrayOutputStream());
            }
        }

        CMSTheme theme = createTheme(prefix, isOnlyDirectory, files, themeDescription);

        return theme;
    }

    @Atomic(mode = TxMode.WRITE)
    private static CMSTheme createTheme(String prefix, boolean isOnlyDirectory, HashMap<String, ByteArrayOutputStream> files,
            JsonObject themeDef) {
        try {
            if (themeDef == null) {
                throw new RuntimeException("Theme did not contain a theme.json");
            }

            String themeType = themeDef.get("type").getAsString();

            CMSTheme theme = Optional.ofNullable(CMSTheme.forType(themeType)).orElseGet(() -> new CMSTheme());
            theme.setDescription(themeDef.get("description").getAsString());
            theme.setName(themeDef.get("name").getAsString());
            theme.setBennu(Bennu.getInstance());
            theme.setType(themeType);

            HashSet<CMSTemplate> refused = Sets.newHashSet(theme.getTemplatesSet());
            theme.getFilesSet().stream().forEach(file -> file.delete());

            loadExtends(themeDef, theme);
            HashMap<String, CMSTemplateFile> processedFiles = loadFiles(prefix, isOnlyDirectory, files, theme);
            loadPageTemplates(themeDef, theme, refused, processedFiles);

            for (CMSTemplate t : refused) {
                if (t.getPagesSet().size() != 0) {
                    throw new RuntimeException("Cannot replace theme, '" + t.getType()
                            + "' is being used in some pages but is not included in new theme.");
                } else {
                    t.delete();
                }
            }

            if (Bennu.getInstance().getCMSThemesSet().size() == 1) {
                Bennu.getInstance().setDefaultCMSTheme(theme);
            }
            return theme;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
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

    private static void loadPageTemplates(JsonObject themeDef, CMSTheme theme, HashSet<CMSTemplate> refused,
            HashMap<String, CMSTemplateFile> processedFiles) {
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
            CMSTemplateFile file = processedFiles.get(path);

            if (file == null) {
                throw new RuntimeException("File in template '" + type + "' isn't in the Zip.");
            }
            tp.setFile(file);
        }
    }

    private static HashMap<String, CMSTemplateFile> loadFiles(String prefix, boolean isOnlyDirectory,
            HashMap<String, ByteArrayOutputStream> files, CMSTheme theme) {
        HashMap<String, CMSTemplateFile> processedFiles = new HashMap<>();
        for (String name : files.keySet()) {
            Path p = Paths.get(name);
            String filename = Optional.of(p.getFileName().toString()).orElse("");
            CMSTemplateFile file = new CMSTemplateFile(filename, name, name, files.get(name).toByteArray());
            theme.addFiles(file);
            processedFiles.put(name, file);
        }
        return processedFiles;
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

        public abstract ByteArrayOutputStream getArrayOutputStream();

    }

    private static class ZipEntryBean extends EntryBean {

        private final ByteArrayOutputStream bs = new ByteArrayOutputStream();

        public ZipEntryBean(ZipFile zipFile, ZipEntry zipEntry) {
            super(zipEntry.getName(), zipEntry.isDirectory());
            try {
                int len;
                byte[] buffer = new byte[2048];
                while ((len = zipFile.getInputStream(zipEntry).read(buffer)) > 0) {
                    bs.write(buffer, 0, len);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error reading the content of the zip file entry", e.getCause());
            }
        }

        public ZipEntryBean(ZipInputStream zin, ZipEntry zipEntry) {
            super(zipEntry.getName(), zipEntry.isDirectory());
            try {
                int len;
                byte[] buffer = new byte[2048];
                while ((len = zin.read(buffer)) > 0) {
                    bs.write(buffer, 0, len);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error reading the content of the zip file entry", e.getCause());
            }
        }

        @Override
        public ByteArrayOutputStream getArrayOutputStream() {
            return bs;
        }

    }

    private static class FileEntryBean extends EntryBean {
        private final File file;

        public FileEntryBean(File file) {
            super(file.getName(), file.isDirectory());
            this.file = file;
        }

        @Override
        public ByteArrayOutputStream getArrayOutputStream() {
            try {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                FileInputStream inputStream = new FileInputStream(file);
                byte[] buffer = new byte[2048];
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    byteStream.write(buffer, 0, len);
                }
                inputStream.close();
                return byteStream;
            } catch (Exception e) {
                throw new RuntimeException("Error reading the content of the file entry", e.getCause());
            }
        }

    }

}
