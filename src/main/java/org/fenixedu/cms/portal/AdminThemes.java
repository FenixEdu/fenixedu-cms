package org.fenixedu.cms.portal;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.cms.domain.CMSTemplate;
import org.fenixedu.cms.domain.CMSTemplateFile;
import org.fenixedu.cms.domain.CMSTheme;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Controller
@RequestMapping("/cms/manage")
public class AdminThemes {

    final int BUFFER = 2048;

    final static Pattern RELATIVE_PARENT = Pattern.compile("^../|/../|/..$");
    final static Pattern RELATIVE_CURRENT = Pattern.compile("^./|/./|/.$");
    final static Pattern FULL_PATH = Pattern.compile("^/.*");

    @RequestMapping(value = "themes", method = RequestMethod.GET)
    public String themes(Model model) {
        model.addAttribute("themes", Bennu.getInstance().getCMSThemesSet());
        return "themes";
    }

    @RequestMapping(value = "themes/{type}/see", method = RequestMethod.GET)
    public String viewTheme(Model model, @PathVariable(value = "type") String type) {
        model.addAttribute("theme", CMSTheme.forType(type));
        return "viewTheme";
    }

    @RequestMapping(value = "themes/{type}/delete", method = RequestMethod.GET)
    public RedirectView deleteTheme(Model model, @PathVariable(value = "type") String type) {
        CMSTheme.forType(type).delete();
        return new RedirectView("/xpto/cms/manage/themes", false);
    }

    @RequestMapping(value = "themes/create", method = RequestMethod.GET)
    public String addTheme(Model model) {
        return "addTheme";
    }

    @RequestMapping(value = "themes/create", method = RequestMethod.POST)
    public RedirectView addTheme(@RequestParam Boolean isDefault, @RequestParam("uploadedFile") CommonsMultipartFile uploadedFile)
            throws IOException {

        File temp = File.createTempFile("tempCmsTheme", ".zip");
        uploadedFile.transferTo(temp);

        FileInputStream fis = new FileInputStream(temp);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
        ZipEntry entry;

        String prefix = null;
        boolean isOnlyDirectory = true;

        HashMap<String, ByteArrayOutputStream> files = new HashMap<>();

        ByteArrayOutputStream themeDescription = null;

        while ((entry = zis.getNextEntry()) != null) {

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

            int len;
            byte[] buffer = new byte[BUFFER];
            ByteArrayOutputStream bs = new ByteArrayOutputStream();

            while ((len = zis.read(buffer)) > 0) {
                bs.write(buffer, 0, len);
            }

            if (entry.getName().endsWith("theme.json")) {
                themeDescription = bs;
            } else {
                files.put(entry.getName(), bs);
            }
        }

        CMSTheme theme = createTheme(prefix, isOnlyDirectory, files, themeDescription);

        if (isDefault) {
            Bennu.getInstance().setDefaultCMSTheme(theme);
        }

        zis.close();
        return new RedirectView("/cms/manage/themes", true);
    }

    @Atomic(mode = TxMode.WRITE)
    private CMSTheme createTheme(String prefix, boolean isOnlyDirectory, HashMap<String, ByteArrayOutputStream> files,
            ByteArrayOutputStream themeDescription) throws IOException {

        if (themeDescription == null) {
            throw new RuntimeException("Theme did not contain a theme.json");
        }

        JsonObject themeDef = new JsonParser().parse(new String(themeDescription.toByteArray())).getAsJsonObject();
        String themeType = themeDef.get("type").getAsString();
        CMSTheme theme = CMSTheme.forType(themeType);

        if (theme != null) {
            theme.delete();
        }

        theme = new CMSTheme();
        theme.setBennu(Bennu.getInstance());
        theme.setName(themeDef.get("name").getAsString());
        theme.setDescription(themeDef.get("description").getAsString());
        theme.setType(themeType);

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

            String[] path = name.split("/");

            CMSTemplateFile file = new CMSTemplateFile(name, name, files.get(fileName).toByteArray());
            theme.addFiles(file);
            processedFiles.put(name, file);
            System.out.println("Extracting: " + fileName);
        }

        for (Entry<String, JsonElement> entry : themeDef.get("templates").getAsJsonObject().entrySet()) {
            CMSTemplate tp = new CMSTemplate();

            String type = entry.getKey();
            JsonObject obj = entry.getValue().getAsJsonObject();

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

    @RequestMapping(value = "themes/{type}/editFile/**", method = RequestMethod.GET)
    public String editFile(Model model, @PathVariable(value = "type") String type, HttpServletRequest request) {
        CMSTheme theme = CMSTheme.forType(type);
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        path = path.substring(("/cms/manage/themes/" + theme.getType() + "/editFile/").length());
        CMSTemplateFile file = CMSTheme.forType(type).fileForPath(path);
        
        model.addAttribute("theme", CMSTheme.forType(type));
        model.addAttribute("linkBack", "/cms/manage/themes/" + theme.getType() + "/see");
        model.addAttribute("file", file);

        String contentType = file.getContentType();
        if (contentType.equals("text/html")) {
            model.addAttribute("type", "twig");
            model.addAttribute("content", new String(file.getContent()));
        } else if (contentType.equals("text/javascript")) {
            model.addAttribute("type", "javascript");
            model.addAttribute("content", new String(file.getContent()));
        } else if (contentType.equals("text/css")){
            model.addAttribute("type", "css");            
            model.addAttribute("content", new String(file.getContent()));
        }
        
        return "editThemeFile";
    }
}
