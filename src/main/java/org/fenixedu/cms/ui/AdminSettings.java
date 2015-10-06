package org.fenixedu.cms.ui;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.CmsSettings;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.fenixedu.cms.rendering.CMSExtensions;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.FenixFramework;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/settings")
public class AdminSettings {

  @RequestMapping
  public String view(Model model) {
    ensureManager();
    model.addAttribute("cmsSettings", Bennu.getInstance().getCmsSettings());
    return "fenixedu-cms/settings";
  }

  @RequestMapping(method = RequestMethod.POST)
  public RedirectView view(@RequestParam String themesManagers, @RequestParam String rolesManagers,
                     @RequestParam String foldersManagers, @RequestParam String settingsManagers) {
    FenixFramework.atomic(()->{
      ensureManager();
      CmsSettings settings = Bennu.getInstance().getCmsSettings();
      settings.setThemesManagers(group(themesManagers));
      settings.setRolesManagers(group(rolesManagers));
      settings.setFoldersManagers(group(foldersManagers));
      settings.setSettingsManagers(group(settingsManagers));
    });
    return new RedirectView("/cms/settings", true);
  }


  private static PersistentGroup group(String expression) {
    Group group = Group.parse(expression);
    if(!group.isMember(Authenticate.getUser())) {
      CmsDomainException.forbiden();
    }
    return group.toPersistentGroup();
  }

  private static void ensureManager() {
    if(!Group.managers().isMember(Authenticate.getUser())) {
      CmsDomainException.forbiden();
    }
  }
}
