package org.fenixedu.bennu.cms.portal;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.cms.domain.*;
import org.fenixedu.bennu.cms.routing.CMSURLHandler;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = AdminSites.class, title = "application.admin-themes.title")
@RequestMapping("/cms/themes")
public class AdminThemes {

    private final Map<String, String> supportedContentTypes;
    private final CMSURLHandler urlHandler;

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
    }

    @RequestMapping(method = RequestMethod.GET)
    public String themes(Model model) {
        model.addAttribute("themes", Bennu.getInstance().getCMSThemesSet());
        return "themes";
    }

    @RequestMapping(value = "{type}/see", method = RequestMethod.GET)
    public String viewTheme(Model model, @PathVariable(value = "type") String type) {
        model.addAttribute("theme", CMSTheme.forType(type));
        model.addAttribute("supportedTypes", supportedContentTypes.keySet());
        return "viewTheme";
    }

    @RequestMapping(value = "loadDefault", method = RequestMethod.GET)
    public RedirectView loadDefaultThemes(Model model) {
        //TODO
        return new RedirectView("/cms/themes", true);
    }

    @RequestMapping(value = "{type}/delete", method = RequestMethod.POST)
    public RedirectView deleteTheme(Model model, @PathVariable(value = "type") String type) {
        CMSTheme.forType(type).delete();
        return new RedirectView("/cms/themes", true);
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String addTheme(Model model) {
        return "addTheme";
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public RedirectView addTheme(@RequestParam Boolean isDefault, @RequestParam("uploadedFile") MultipartFile uploadedFile)
            throws IOException {
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".zip");
        Files.write(uploadedFile.getBytes(), tempFile);
        CMSTheme theme = CMSThemeLoader.createFromZip(new ZipFile(tempFile));
        if (isDefault) {
            Bennu.getInstance().setDefaultCMSTheme(theme);
        }
        return new RedirectView("/cms/themes", true);
    }

    @RequestMapping(value = "{type}/editFile/**", method = RequestMethod.GET)
    public String editFile(Model model, @PathVariable(value = "type") String type, HttpServletRequest request) {
        CMSTheme theme = CMSTheme.forType(type);
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        path = path.substring(("/cms/themes/" + theme.getType() + "/editFile/").length());
        CMSThemeFile file = CMSTheme.forType(type).fileForPath(path);

        model.addAttribute("theme", CMSTheme.forType(type));
        model.addAttribute("linkBack", "/cms/themes/" + theme.getType() + "/see");
        model.addAttribute("file", file);

        String contentType = file.getContentType();

        if (supportedContentTypes.containsKey(contentType)) {
            model.addAttribute("type", supportedContentTypes.get(contentType));
            model.addAttribute("content", new String(file.getContent(), StandardCharsets.UTF_8));
        }

        return "editThemeFile";
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
        return "newTheme";
    }

    @RequestMapping(value = "new", method = RequestMethod.POST)
    public RedirectView newTheme(Model model, @RequestParam String type, @RequestParam String name,
            @RequestParam String description) {
        newTheme(type, name, description);
        return new RedirectView("/cms/themes/" + type + "/see", true);
    }

    @Atomic
    public void newTheme(String type, String name, String description){
        CMSTheme theme = new CMSTheme();
        theme.setType(type);
        theme.setName(name);
        theme.setDescription(description);
        theme.setBennu(Bennu.getInstance());
        theme.changeFiles(new CMSThemeFiles(new HashMap<String, CMSThemeFile>()));
    }

    @RequestMapping(value = "{type}/newFile", method = RequestMethod.POST)
    public RedirectView newFile(@PathVariable(value = "type") String type, @RequestParam String filename){
        CMSTheme theme = CMSTheme.forType(type);
        String[] r = filename.split("/");
        if (theme.fileForPath(filename) == null){
            CMSThemeFile newFile = new CMSThemeFile(r[r.length - 1], filename, new byte[0]);
            theme.changeFiles(theme.getFiles().with(newFile));
            return new RedirectView("/cms/themes/" + type + "/editFile/" + filename, true);
        }else{
            throw new RuntimeException("File already exists");
        }
    }

    @RequestMapping(value = "{type}/importFile", method = RequestMethod.POST)
    public RedirectView importTheme(@PathVariable(value = "type") String type, @RequestParam String filename, @RequestParam("uploadedFile") MultipartFile uploadedFile)
            throws IOException {
        CMSTheme theme = CMSTheme.forType(type);
        String[] r = filename.split("/");
        if (theme.fileForPath(filename) == null){
            CMSThemeFile newFile = new CMSThemeFile(r[r.length - 1], filename, uploadedFile.getBytes());
            theme.changeFiles(theme.getFiles().with(newFile));
            return new RedirectView("/cms/themes/" + type + "/see", true);
        }else{
            throw new RuntimeException("File already exists");
        }
    }

    @RequestMapping(value = "{type}/newTemplate", method = RequestMethod.POST)
    public RedirectView newTemplate(@PathVariable(value = "type") String type, @RequestParam(value="type") String templateType, @RequestParam String name,
            @RequestParam String description, @RequestParam String filename){
        CMSTheme theme = CMSTheme.forType(type);
        if (theme.templateForType(templateType) == null){
            newTemplate(templateType, name, description, filename, theme);
            return new RedirectView("/cms/themes/" + type + "/see#templates", true);
        }else{
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
    public RedirectView deleteTemplate(@PathVariable(value = "type") String type, @RequestParam(value="type") String templateType) {
        CMSTheme theme = CMSTheme.forType(type);
        deleteTemplate(templateType, theme);
        return new RedirectView("/cms/themes/" + type + "/see#templates", true);
    }
    @Atomic
    private void deleteTemplate(String templateType, CMSTheme theme) {
        theme.templateForType(templateType).delete();
    }
}
