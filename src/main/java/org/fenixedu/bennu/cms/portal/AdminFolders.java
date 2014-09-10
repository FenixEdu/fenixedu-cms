package org.fenixedu.bennu.cms.portal;

import static pt.ist.fenixframework.FenixFramework.atomic;

import javax.script.ScriptException;

import org.fenixedu.bennu.cms.domain.CMSFolder;
import org.fenixedu.bennu.cms.domain.CMSFolder.FolderResolver;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.NashornStrategy;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.base.Strings;

@SpringFunctionality(app = AdminSites.class, title = "application.admin-folders.title", accessGroup = "#managers")
@RequestMapping("/cms/folders")
public class AdminFolders {

    @RequestMapping(method = RequestMethod.GET)
    public String listFolders(Model model) {
        model.addAttribute("folders", Bennu.getInstance().getCmsFolderSet());
        return "folders";
    }

    @RequestMapping(method = RequestMethod.POST)
    public RedirectView createFolder(@RequestParam String path, @RequestParam LocalizedString description) {
        atomic(() -> {
            new CMSFolder(PortalConfiguration.getInstance().getMenu(), path, description);
        });
        return new RedirectView("/cms/folders", true);
    }

    @RequestMapping(value = "/resolver/{folder}", method = RequestMethod.GET)
    public String folderResolver(Model model, @PathVariable CMSFolder folder) {
        model.addAttribute("folder", folder);
        return "folderResolver";
    }

    @RequestMapping(value = "/resolver/{folder}", method = RequestMethod.PUT)
    public ResponseEntity<?> saveFolderResolver(@PathVariable CMSFolder folder, @RequestBody String code) {
        try {
            atomic(() -> {
                if (Strings.isNullOrEmpty(code)) {
                    folder.setResolver(null);
                } else {
                    folder.setResolver(new NashornStrategy<>(FolderResolver.class, code));
                }
                return null;
            });
        } catch (Exception e) {
            Throwable original = unwrap(e);
            return new ResponseEntity<>(original.getClass().getName() + ": " + original.getMessage(),
                    HttpStatus.PRECONDITION_FAILED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Throwable unwrap(Throwable e) {
        while (e.getCause() != null) {
            if (e instanceof ScriptException) {
                return e;
            }
            e = e.getCause();
        }
        return e;
    }

    @RequestMapping("/delete/{folder}")
    public RedirectView deleteFolder(@PathVariable CMSFolder folder) {
        atomic(() -> folder.delete());
        return new RedirectView("/cms/folders", true);
    }
}
