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
package org.fenixedu.cms.ui;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.*;
import org.fenixedu.cms.exceptions.ResourceNotFoundException;
import org.fenixedu.cms.routing.CMSURLHandler;
import org.fenixedu.commons.stream.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import pt.ist.fenixframework.Atomic;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static java.util.stream.Collectors.toList;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/themes")
public class AdminThemes {

    private static final long NUM_TOP_SITES = 4;
    private final Map<String, String> supportedContentTypes;
    private final Map<String, String> supportedImagesContentTypes;
    private final CMSURLHandler urlHandler;

    private final static Logger logger = LoggerFactory.getLogger(AdminThemes.class);

    @Autowired
    public AdminThemes(CMSURLHandler urlHandler) {
        this.urlHandler = urlHandler;

        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        builder.put("text/plain", "plain_text");

        builder.put("text/html", "twig");

        builder.put("text/javascript", "javascript");
        builder.put("application/javascript", "javascript");

        builder.put("application/json", "json");

        builder.put("text/css", "css");

        this.supportedContentTypes = builder.build();

        builder = ImmutableMap.builder();

        builder.put("image/jpeg", "JPEG");

        builder.put("image/jp2", "JPEG 2000");
        builder.put("image/jpx", "JPEG 2000");
        builder.put("image/jpm", "JPEG 2000");
        builder.put("video/mj2", "JPEG 2000");

        builder.put("image/vnd.ms-photo", "JPEG XR");
        builder.put("image/jxr", "JPEG XR");

        builder.put("image/webp", "WebP");

        builder.put("image/gif", "GIF");

        builder.put("image/png", "PNG");

        builder.put("video/x-mng", "MNG");

        builder.put("image/tiff", "TIFF");
        builder.put("image/tiff-fx", "TIFF");

        builder.put("image/svg+xml", "SVG");

        builder.put("application/pdf", "PDF");
        builder.put("image/x‑xbitmap", "X-BMP");
        builder.put("image/bmp", "BMP");

        this.supportedImagesContentTypes = builder.build();

    }

    @RequestMapping(method = RequestMethod.GET)
    public String themes(Model model) {
        model.addAttribute("themes", Bennu.getInstance().getCMSThemesSet());
        return "fenixedu-cms/themes";
    }

    @RequestMapping(value = "{type}/see", method = RequestMethod.GET)
    public String viewTheme(Model model, @PathVariable(value = "type") String type) {
        CMSTheme theme = CMSTheme.forType(type);
        model.addAttribute("theme", theme);
        model.addAttribute("sites", theme.getAllSitesStream()
                .sorted(Site.NAME_COMPARATOR).limit(NUM_TOP_SITES).collect(toList()));
        return "fenixedu-cms/viewTheme";
    }

    @RequestMapping(value = "loadDefault", method = RequestMethod.GET)
    public RedirectView loadDefaultThemes(Model model) {
        // TODO
        return new RedirectView("/cms/themes", true);
    }

    @RequestMapping(value = "{type}/delete", method = RequestMethod.POST)
    public RedirectView deleteTheme(Model model, @PathVariable(value = "type") String type) {
        CMSTheme.forType(type).delete();
        return new RedirectView("/cms/themes", true);
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String importTheme(Model model) {
        return "fenixedu-cms/importTheme";
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public RedirectView addTheme(@RequestParam("uploadedFile") MultipartFile uploadedFile) throws IOException {
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".zip");
        Files.write(uploadedFile.getBytes(), tempFile);
        CMSTheme theme = CMSThemeLoader.createFromZip(new ZipFile(tempFile));

        return new RedirectView("/cms/themes", true);
    }

    @RequestMapping(value = "{type}/editFile/**", method = RequestMethod.GET)
    public String editFile(Model model, @PathVariable(value = "type") String type, HttpServletRequest request) {
        CMSTheme theme = CMSTheme.forType(type);
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        path = path.substring(("/cms/themes/" + theme.getType() + "/editFile/").length());
        CMSThemeFile file = CMSTheme.forType(type).fileForPath(path);

        model.addAttribute("theme", CMSTheme.forType(type));
        model.addAttribute("linkBack", "/cms/themes/" + theme.getType() + "/edit");
        model.addAttribute("file", file);

        String contentType = file.getContentType();

        if (supportedContentTypes.containsKey(contentType)) {
            model.addAttribute("type", supportedContentTypes.get(contentType));
            model.addAttribute("content", new String(file.getContent(), StandardCharsets.UTF_8));
            return "fenixedu-cms/editThemeFile";
        } else if (supportedImagesContentTypes.containsKey(contentType)) {

            if (contentType.equals("image/svg+xml")) {
                model.addAttribute("isSVG", true);
                model.addAttribute("content", new String(file.getContent()));
            } else {
                model.addAttribute("isSVG", false);
                model.addAttribute("content", Base64.getEncoder().encodeToString(file.getContent()));
            }

            model.addAttribute("file", file);
            try {
                BufferedImage read = ImageIO.read(new ByteArrayInputStream(file.getContent()));
                model.addAttribute("height", read.getHeight());
                model.addAttribute("width", read.getHeight());

                ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(file.getContent()));
                Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

                if (readers.hasNext()) {

                    // pick the first available ImageReader
                    ImageReader reader = readers.next();

                    // attach source to the reader
                    reader.setInput(iis, true);

                    // read metadata of first image
                    IIOMetadata metadata = reader.getImageMetadata(0);

                    String name = metadata.getNativeMetadataFormatName();
                    JsonObject obj = generateMetadata(metadata.getAsTree(name));

                    obj.addProperty("name", file.getContentType());

                    model.addAttribute("metadata", obj);
                }

            } catch (Exception e) {

            }

            return "fenixedu-cms/viewThemeImageFile";
        } else {
            throw new ResourceNotFoundException();
        }

    }

    private JsonObject generateMetadata(Node node) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", node.getNodeName());
        NamedNodeMap map = node.getAttributes();
        if (map != null) {

            // print attribute values
            int length = map.getLength();
            for (int i = 0; i < length; i++) {
                Node attr = map.item(i);
                obj.addProperty(attr.getNodeName(), attr.getNodeValue());
            }
        }

        Node child = node.getFirstChild();
        if (child == null) {
            return obj;
        }

        JsonArray array = new JsonArray();
        while (child != null) {
            JsonObject kid = generateMetadata(child);
            array.add(kid);

            child = child.getNextSibling();
        }
        obj.add("children", array);

        return obj;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{type}/editFile/**", method = RequestMethod.PUT)
    public void saveFileEdition(@PathVariable(value = "type") String type, HttpServletRequest request, @RequestBody String content) {
        CMSTheme theme = CMSTheme.forType(type);
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        path = path.substring(("/cms/themes/" + theme.getType() + "/editFile/").length());
        CMSThemeFile file = theme.fileForPath(path);

        CMSThemeFile newFile = new CMSThemeFile(file.getFileName(), file.getFullPath(), content.getBytes(StandardCharsets.UTF_8));

        theme.changeFiles(theme.getFiles().with(newFile));

        urlHandler.invalidateEntry(type + "/" + file.getFullPath());
    }

    @RequestMapping(value = "{type}/deleteFile", method = RequestMethod.POST)
    public RedirectView deleteFile(@PathVariable(value = "type") String type, @RequestParam String path) {
        CMSTheme theme = CMSTheme.forType(type);
        theme.changeFiles(theme.getFiles().without(path));
        return new RedirectView("/cms/themes/" + type + "/see", true);
    }

    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newTheme(Model model) {
        model.addAttribute("themes", Bennu.getInstance().getCMSThemesSet());
        return "fenixedu-cms/newTheme";
    }

    @RequestMapping(value = "new", method = RequestMethod.POST)
    public RedirectView newTheme(Model model, @RequestParam String type, @RequestParam String name,
            @RequestParam String description, @RequestParam(value = "extends") String ext) {
        CMSTheme theme = CMSTheme.forType(ext);
        newTheme(type, name, description, theme);
        return new RedirectView("/cms/themes/" + type + "/see", true);
    }

    @Atomic
    public void newTheme(String type, String name, String description, CMSTheme ext) {
        CMSTheme theme = new CMSTheme();
        theme.setType(type);
        theme.setName(name);
        theme.setDescription(description);
        theme.setBennu(Bennu.getInstance());
        theme.setExtended(ext);
        theme.changeFiles(new CMSThemeFiles(new HashMap<String, CMSThemeFile>()));
    }

    @RequestMapping(value = "{type}/newFile", method = RequestMethod.POST)
    public RedirectView newFile(@PathVariable(value = "type") String type, @RequestParam String filename) {
        CMSTheme theme = CMSTheme.forType(type);
        String[] r = filename.split("/");
        if (theme.fileForPath(filename) == null) {
            CMSThemeFile newFile = new CMSThemeFile(r[r.length - 1], filename, new byte[0]);
            theme.changeFiles(theme.getFiles().with(newFile));
            return new RedirectView("/cms/themes/" + type + "/editFile/" + filename, true);
        } else {
            throw new RuntimeException("File already exists");
        }
    }

    @RequestMapping(value = "{type}/importFile", method = RequestMethod.POST)
    public RedirectView importFile(@PathVariable(value = "type") String type, @RequestParam String filename,
            @RequestParam("uploadedFile") MultipartFile uploadedFile) throws IOException {
        CMSTheme theme = CMSTheme.forType(type);
        String[] r = filename.split("/");
        if (theme.fileForPath(filename) == null) {
            addFile(filename, uploadedFile, theme, r);
            return new RedirectView("/cms/themes/" + type + "/see", true);
        } else {
            throw new RuntimeException("File already exists");
        }
    }

    @Atomic
    private void addFile(String filename, MultipartFile uploadedFile, CMSTheme theme, String[] r) throws IOException {
        CMSThemeFile newFile = new CMSThemeFile(r[r.length - 1], filename, uploadedFile.getBytes());
        theme.changeFiles(theme.getFiles().with(newFile));
    }

    @RequestMapping(value = "{type}/newTemplate", method = RequestMethod.POST)
    public RedirectView newTemplate(@PathVariable(value = "type") String type, @RequestParam(value = "type") String templateType,
            @RequestParam String name, @RequestParam String description, @RequestParam String filename) {
        CMSTheme theme = CMSTheme.forType(type);
        if (theme.templateForType(templateType) == null) {
            newTemplate(templateType, name, description, filename, theme);
            return new RedirectView("/cms/themes/" + type + "/see#templates", true);
        } else {
            throw new RuntimeException("Template already exists");
        }
    }

    @Atomic
    private void newTemplate(String templateType, String name, String description, String filename, CMSTheme theme) {
        CMSTemplate tp = new CMSTemplate();
        tp.setName(name);
        tp.setDescription(description);
        tp.setType(templateType);
        tp.setTheme(theme);
        tp.setFilePath(filename);
    }

    @RequestMapping(value = "{type}/deleteTemplate", method = RequestMethod.POST)
    public RedirectView deleteTemplate(@PathVariable(value = "type") String type,
            @RequestParam(value = "type") String templateType) {
        CMSTheme theme = CMSTheme.forType(type);
        deleteTemplate(templateType, theme);
        return new RedirectView("/cms/themes/" + type + "/see#templates", true);
    }

    @Atomic
    private void deleteTemplate(String templateType, CMSTheme theme) {
        theme.templateForType(templateType).delete();
    }

    @RequestMapping(value = "{type}/duplicate", method = RequestMethod.POST)
    public RedirectView duplicateTheme(Model model, @PathVariable String type,
            @RequestParam(value = "newThemeType") String newThemeType, @RequestParam String name, @RequestParam String description) {
        CMSTheme orig = CMSTheme.forType(type);
        duplicateTheme(orig, newThemeType, name, description);
        return new RedirectView("/cms/themes/" + newThemeType + "/see", true);
    }

    @Atomic
    public void duplicateTheme(CMSTheme orig, String type, String name, String description) {
        CMSTheme theme = new CMSTheme();
        theme.setType(type);
        theme.setName(name);
        theme.setDescription(description);
        theme.setBennu(orig.getBennu());
        theme.setExtended(orig.getExtended());
        theme.changeFiles(orig.getFiles());
        for (CMSTemplate originalTemplate : orig.getTemplatesSet()) {
            CMSTemplate tp = new CMSTemplate();
            tp.setTheme(theme);
            tp.setFilePath(originalTemplate.getFilePath());
            tp.setType(originalTemplate.getType());
            tp.setDescription(originalTemplate.getDescription());
            tp.setName(originalTemplate.getName());
        }
    }

    @RequestMapping(value = "{type}/moveFile", method = RequestMethod.POST)
    public RedirectView moveFile(Model model, @PathVariable String type, @RequestParam String origFilename,
            @RequestParam String filename) {
        CMSTheme theme = CMSTheme.forType(type);
        CMSThemeFile file = theme.fileForPath(origFilename);

        moveFile(filename, file, theme);

        return new RedirectView("/cms/themes/" + type + "/see", true);
    }

    @Atomic
    private void moveFile(String filename, CMSThemeFile file, CMSTheme theme) {
        String[] r = filename.split("/");
        CMSThemeFile newFile = new CMSThemeFile(r[r.length - 1], filename, file.getContent());
        theme.changeFiles(theme.getFiles().without(file.getFullPath()).with(newFile));
    }

    @RequestMapping(value = "{type}/edit", method = RequestMethod.GET)
    public String editTheme(Model model, @PathVariable(value = "type") String type) {
        model.addAttribute("theme", CMSTheme.forType(type));
        model.addAttribute("supportedTypes", supportedContentTypes.keySet());
        return "fenixedu-cms/editTheme";
    }

    @RequestMapping(value = "{type}/listFiles", method = RequestMethod.GET)
    @ResponseBody
    public String listFiles(Model model, @PathVariable(value = "type") String type) {
        CMSTheme theme = CMSTheme.forType(type);
        Collection<CMSThemeFile> totalFiles = theme.getFiles().getFiles();
        JsonArray result = new JsonArray();
        for (CMSThemeFile file : totalFiles) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", file.getFileName());
            obj.addProperty("path", file.getFullPath());
            obj.addProperty("size", file.getFileSize());
            obj.addProperty("contentType", file.getContentType());
            obj.addProperty("modified", file.getLastModified().toString());
            result.add(obj);
        }

        return result.toString();
    }

    @RequestMapping(value = "{type}/editSettings", method = RequestMethod.POST)
    public RedirectView editThemeSettings(@PathVariable(value = "type") String type, @RequestParam String name,
            @RequestParam String description, @RequestParam(value = "extends") String ext, @RequestParam(value = "thumbnail",
            required = false) MultipartFile thumbnail,
            @RequestParam(value = "defaultTemplate", required = false) String defaultTemplate) {

        CMSTheme theme = CMSTheme.forType(type);

        CMSTheme extTheme = null;

        if (!ext.equals("")) {
            extTheme = CMSTheme.forType(ext);
        }

        editTheme(theme, name, description, extTheme, thumbnail, defaultTemplate);

        return new RedirectView("/cms/themes/" + type + "/see");
    }

    @Atomic
    private void editTheme(CMSTheme theme, String name, String description, CMSTheme extTheme, MultipartFile thumbnail,
            String defaultTheme) {
        theme.setName(name);
        theme.setDescription(description);
        theme.setDefaultTemplate(theme.templateForType(defaultTheme));

        if (extTheme != null) {
            theme.setExtended(extTheme);
        }
        if (!thumbnail.isEmpty()) {
            GroupBasedFile old = theme.getPreviewImage();
            if (old != null) {
                old.setThemePreview(null);
                old.delete();
            }
            GroupBasedFile newthumbnail = null;
            try {
                newthumbnail =
                        new GroupBasedFile(thumbnail.getOriginalFilename(), thumbnail.getOriginalFilename(),
                                thumbnail.getBytes(), Group.anyone());
            } catch (IOException e) {
                logger.error("Can't create thumbnail file", e);
            }
            theme.setPreviewImage(newthumbnail);
            theme.setPreviewImagePath(null);
        }
    }

    @RequestMapping(value = "{type}/deleteDir", method = RequestMethod.POST)
    public RedirectView deleteDir(@PathVariable(value = "type") String type, @RequestParam(value = "path") String path) {
        CMSTheme theme = CMSTheme.forType(type);
        deleteDirectory(theme, path);
        return new RedirectView("/cms/themes/" + type + "/see#templates", true);
    }

    @Atomic
    private void deleteDirectory(CMSTheme theme, String directory) {
        List<String> result = Lists.newArrayList();

        for (CMSThemeFile file : theme.getFiles().getFiles()) {
            if (file.getFullPath().startsWith(directory)) {
                result.add(file.getFullPath());
            }
        }

        theme.setFiles(theme.getFiles().without(result.toArray(new String[result.size()])));
    }

    @RequestMapping(value = "{type}/templates", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getTemplates(@PathVariable(value = "type") String type) {
        CMSTheme theme = CMSTheme.forType(type);
        JsonObject obj = new JsonObject();

        obj.add("templates", theme.getAllTemplates().stream().sorted(Comparator.comparing(x -> x.getType())).map(x -> {
            JsonObject o = new JsonObject();
            o.addProperty("type", x.getType());
            o.addProperty("description", x.getDescription());
            o.addProperty("name", x.getName());
            o.addProperty("file", x.getFilePath());
            o.addProperty("default", x.isDefault());
            return o;
        }).collect(StreamUtils.toJsonArray()));

        return obj.toString();
    }

    @RequestMapping(value = "{type}/export", method = RequestMethod.GET, produces = "application/zip")
    @ResponseBody
    public byte[] export(@PathVariable(value = "type") String type) throws IOException {
        CMSTheme theme = CMSTheme.forType(type);

        BufferedInputStream origin = null;
        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
        //out.setMethod(ZipOutputStream.DEFLATED);
        byte data[] = new byte[4096];
        // get a list of files from current directory

        for (CMSThemeFile file : theme.getFiles().getFiles()) {
            if (file.getFullPath().equals("theme.json")) {
                continue;
            }
            ByteArrayInputStream bis = new ByteArrayInputStream(file.getContent());
            origin = new BufferedInputStream(bis, 4096);
            ZipEntry entry = new ZipEntry(file.getFullPath());
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, 4096)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();

        }

        JsonObject obj = new JsonObject();

        obj.addProperty("type", theme.getType());
        obj.addProperty("name", theme.getName());
        obj.addProperty("description", theme.getDescription());

        if (theme.getPreviewImagePath() != null && theme.fileForPath(theme.getPreviewImagePath()) != null) {
            obj.addProperty("thumbnail", theme.getPreviewImagePath());
        } else if (theme.getPreviewImage() != null) {
            String filename = "thumbnail";
            if (theme.fileForPath(filename) != null) {
                filename = new BigInteger(130, new SecureRandom()).toString(32);
            }

            ByteArrayInputStream bis = new ByteArrayInputStream(theme.getPreviewImage().getContent());
            origin = new BufferedInputStream(bis, 4096);
            ZipEntry entry = new ZipEntry(filename);
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, 4096)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();
            obj.addProperty("thumbnail", filename);
        }

        JsonObject templates = new JsonObject();

        for (CMSTemplate template : theme.getTemplatesSet()) {
            JsonObject tmpl = new JsonObject();

            tmpl.addProperty("name", template.getName());
            tmpl.addProperty("description", template.getName());
            tmpl.addProperty("file", template.getFilePath());

            templates.add(template.getType(), tmpl);
        }

        obj.add("templates", templates);

        ZipEntry entry = new ZipEntry("theme.json");
        out.putNextEntry(entry);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        byte[] themedef = gson.toJson(obj).getBytes();

        out.write(themedef, 0, themedef.length);

        out.close();

        return dest.toByteArray();
    }

}
