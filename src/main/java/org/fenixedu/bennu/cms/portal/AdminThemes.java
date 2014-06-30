package org.fenixedu.bennu.cms.portal;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.cms.domain.CMSTemplateFile;
import org.fenixedu.bennu.cms.domain.CMSTheme;
import org.fenixedu.bennu.cms.domain.CMSThemeLoader;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.io.Files;

@SpringFunctionality(app = AdminPortal.class, title = "application.admin-themes.title")
@RequestMapping("/themes")
public class AdminThemes {

    @RequestMapping(method = RequestMethod.GET)
    public String themes(Model model) {
        model.addAttribute("themes", Bennu.getInstance().getCMSThemesSet());
        return "themes";
    }

    @RequestMapping(value = "{type}/see", method = RequestMethod.GET)
    public String viewTheme(Model model, @PathVariable(value = "type") String type) {
        model.addAttribute("theme", CMSTheme.forType(type));
        return "viewTheme";
    }

    @RequestMapping(value = "loadDefault", method = RequestMethod.GET)
    public RedirectView loadDefaultThemes(Model model) {
        CMSThemeLoader.createDefaultThemes();
        return new RedirectView("/cms/manage/themes", true);
    }

    @RequestMapping(value = "{type}/delete", method = RequestMethod.POST)
    public RedirectView deleteTheme(Model model, @PathVariable(value = "type") String type) {
        CMSTheme.forType(type).delete();
        return new RedirectView("/cms/manage/themes", true);
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
        CMSThemeLoader.createFromZip(isDefault, new ZipFile(tempFile));
        return new RedirectView("/cms/manage/themes", true);
    }


    @RequestMapping(value = "{type}/editFile/**", method = RequestMethod.GET)
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
        } else if (contentType.equals("text/css")) {
            model.addAttribute("type", "css");
            model.addAttribute("content", new String(file.getContent()));
        }

        return "editThemeFile";
    }
}
