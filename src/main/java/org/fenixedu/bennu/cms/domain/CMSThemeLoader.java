package org.fenixedu.bennu.cms.domain;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.fenixedu.bennu.core.domain.Bennu;

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

    public static void createDefaultThemes() {
        InputStream in = CMSThemeLoader.class.getResourceAsStream("/META-INF/resources/WEB-INF/cms-default-theme.zip");
        ZipInputStream zin = new ZipInputStream(in);
        create(true, getFolderEntries(zin));
    }

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

    public static CMSTheme createFromZip(Boolean isDefault, ZipFile zipFile) {
        return create(isDefault, getZipEntries(zipFile));
    }

    public static CMSTheme createFromFolder(Boolean isDefault, File folder) {
        return create(isDefault, getFolderEntries(folder));
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

    private static CMSTheme create(Boolean isDefault, List<? extends EntryBean> entries) {
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

        if (isDefault) {
            Bennu.getInstance().setDefaultCMSTheme(theme);
        }

        return theme;
    }
    @Atomic(mode = TxMode.WRITE)
    private static CMSTheme createTheme(String prefix, boolean isOnlyDirectory, HashMap<String, ByteArrayOutputStream> files,
            JsonObject themeDef) {

        if (themeDef == null) {
            throw new RuntimeException("Theme did not contain a theme.json");
        }

        String themeType = themeDef.get("type").getAsString();
        CMSTheme theme = CMSTheme.forType(themeType);

//        if (theme != null) {
//            theme.delete();
//        }
//
//        theme = new CMSTheme();

        if (theme == null) {
            theme = new CMSTheme();
        }

        theme.setBennu(Bennu.getInstance());
        theme.setName(themeDef.get("name").getAsString());
        theme.setDescription(themeDef.get("description").getAsString());
        theme.setType(themeType);

        for (CMSTemplate template : Sets.newHashSet(theme.getTemplatesSet())) {
            template.delete();
        }

        for (CMSTemplateFile templateFile : Sets.newHashSet(theme.getFilesSet())) {
            templateFile.delete();
        }

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
        HashMap<String, CMSTemplateFile> processedFiles = new HashMap<>();
        for (String fileName : files.keySet()) {
            String name = null;

            if (isOnlyDirectory) {
                name = fileName.substring(prefix.length() + 1);
            } else {
                name = fileName;
            }

            CMSTemplateFile file = new CMSTemplateFile(name, name, files.get(fileName).toByteArray());
            theme.addFiles(file);
            processedFiles.put(name, file);
        }
        for (Entry<String, JsonElement> entry : themeDef.get("templates").getAsJsonObject().entrySet()) {
            String type = entry.getKey();
            JsonObject obj = entry.getValue().getAsJsonObject();
            
            CMSTemplate tp = new CMSTemplate();
            tp.setName(obj.get("name").getAsString());
            tp.setDescription(obj.get("description").getAsString());
            tp.setType(type);
            tp.setTheme(theme);

            String path = obj.get("file").getAsString();
            CMSTemplateFile file = processedFiles.get(path);

            if (file == null) {
                throw new RuntimeException("File in template '" + type + "' isn't in the Zip.");
            }
            tp.setFile(file);
        }

        if (Bennu.getInstance().getCMSThemesSet().size() == 1) {
            Bennu.getInstance().setDefaultCMSTheme(theme);
        }
        return theme;
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

        private ByteArrayOutputStream bs = new ByteArrayOutputStream();

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
        private File file;

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
