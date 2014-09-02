package org.fenixedu.bennu.cms.portal;

import static pt.ist.fenixframework.FenixFramework.atomic;

import org.fenixedu.bennu.cms.domain.CMSFolder;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

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

    @RequestMapping("/delete/{folder}")
    public RedirectView deleteFolder(@PathVariable CMSFolder folder) {
        atomic(() -> folder.delete());
        return new RedirectView("/cms/folders", true);
    }
}
