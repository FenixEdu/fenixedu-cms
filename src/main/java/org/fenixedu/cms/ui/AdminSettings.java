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

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.CmsSettings;
import org.fenixedu.cms.exceptions.CmsDomainException;
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
  
    CmsSettings.getInstance().ensureCanManageGlobalPermissions();
    
    model.addAttribute("cmsSettings", Bennu.getInstance().getCmsSettings());
    return "fenixedu-cms/settings";
  }

  @RequestMapping(method = RequestMethod.POST)
  public RedirectView view(@RequestParam String themesManagers, @RequestParam String rolesManagers,
                     @RequestParam String foldersManagers, @RequestParam String settingsManagers) {
    CmsSettings.getInstance().ensureCanManageGlobalPermissions();
  
    FenixFramework.atomic(()->{
      CmsSettings settings = CmsSettings.getInstance().getInstance();
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

}
