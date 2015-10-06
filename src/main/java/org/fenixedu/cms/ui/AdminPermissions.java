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

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.CmsSettings;
import org.fenixedu.cms.domain.PermissionEvaluation;
import org.fenixedu.cms.domain.PermissionsArray;
import org.fenixedu.cms.domain.PermissionsArray.Permission;
import org.fenixedu.cms.domain.Role;
import org.fenixedu.cms.domain.RoleTemplate;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import pt.ist.fenixframework.FenixFramework;

import static java.util.stream.Collectors.toList;
import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/permissions")
public class AdminPermissions {

    @RequestMapping(method = RequestMethod.GET)
    public String viewTemplates(Model model) {
        CmsSettings.getInstance().ensureCanManageRoles();
        model.addAttribute("templates", allTemplates());
        model.addAttribute("allPermissions", PermissionsArray.all());
        return "fenixedu-cms/permissions";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public RedirectView createTemplate(@RequestParam LocalizedString description, @RequestParam String permissions) {
        CmsSettings.getInstance().ensureCanManageRoles();
        FenixFramework.atomic(() -> {
            RoleTemplate template = new RoleTemplate();
            template.setDescription(description);
            template.setPermissions(PermissionsArray.fromJson(toJsonArray(permissions)));
        });
        return new RedirectView("/cms/permissions", true);
    }

    @RequestMapping(value = "/{roleTemplateId}/edit", method = RequestMethod.GET)
    public String viewEditTemplate(@PathVariable String roleTemplateId, Model model) {
        CmsSettings.getInstance().ensureCanManageRoles();
        model.addAttribute("roleTemplate", FenixFramework.getDomainObject(roleTemplateId));
        model.addAttribute("allPermissions", PermissionsArray.all());
        return "fenixedu-cms/editRoleTemplate";
    }

    @RequestMapping(value="/site/{slugSite}", method = RequestMethod.GET)
    public String viewSiteRoles(@PathVariable String slugSite, Model model) {
        Site site = Site.fromSlug(slugSite);
        ensureCanDoThis(site, Permission.MANAGE_ROLES);
        Set<RoleTemplate> siteTemplates = site.getRolesSet().stream().map(Role::getRoleTemplate).collect(Collectors.toSet());
        model.addAttribute("site", site);
        model.addAttribute("templates", allTemplates().stream().filter(template->!siteTemplates.contains(template)).collect(Collectors.toList()));
        model.addAttribute("roles", site.getRolesSet().stream().sorted(Comparator.comparing(Role::getName)).collect(Collectors.toList()));
        return "fenixedu-cms/editSiteRoles";
    }

    @RequestMapping(value="/site/{slugSite}/addRole", method = RequestMethod.POST)
    public RedirectView createSiteRole(@PathVariable String slugSite, @RequestParam String roleTemplateId) {
        FenixFramework.atomic(()->{
            Site site = Site.fromSlug(slugSite);
            ensureCanDoThis(site, Permission.MANAGE_ROLES);
            RoleTemplate template = FenixFramework.getDomainObject(roleTemplateId);
            if(!template.getRolesSet().stream().map(Role::getSite).filter(roleSite->roleSite.equals(site)).findAny().isPresent()) {
                new Role(template, site);
            }
        });
        return new RedirectView("/cms/permissions/site/" + slugSite, true);
    }

    @RequestMapping(value = "/{roleTemplateId}/addSite", method = RequestMethod.POST)
    public RedirectView createRole(@PathVariable String roleTemplateId, @RequestParam String siteSlug) {
        FenixFramework.atomic(()->{
            CmsSettings.getInstance().ensureCanManageRoles();
            RoleTemplate template = FenixFramework.getDomainObject(roleTemplateId);
            Site site = Site.fromSlug(siteSlug);
            if(!template.getRolesSet().stream().map(Role::getSite).filter(roleSite->roleSite.equals(site)).findAny().isPresent()) {
                new Role(template, site);
            }
        });
        return new RedirectView("/cms/permissions/site/" + siteSlug, true);
    }

    @RequestMapping(value = "site/{slugSite}/{roleId}/delete", method = RequestMethod.POST)
    public RedirectView removeRole(@PathVariable String slugSite, @PathVariable String roleId) {
        CmsSettings.getInstance().ensureCanManageRoles();
        FenixFramework.atomic(() -> ((Role) FenixFramework.getDomainObject(roleId)).delete());
        return new RedirectView("/cms/permissions/site/" + slugSite, true);
    }

    @RequestMapping(value = "/{roleTemplateId}/edit", method = RequestMethod.POST)
    public RedirectView edit(@PathVariable String roleTemplateId, @RequestParam LocalizedString description, @RequestParam String permissions) {
        FenixFramework.atomic(() -> {
            CmsSettings.getInstance().ensureCanManageRoles();
            RoleTemplate template = FenixFramework.getDomainObject(roleTemplateId);
            template.setDescription(description);
            template.setPermissions(PermissionsArray.fromJson(toJsonArray(permissions)));
        });
        return new RedirectView("/cms/permissions/" + roleTemplateId + "/edit", true);
    }

    @RequestMapping(value = "/{roleTemplateId}/delete", method = RequestMethod.POST)
    public RedirectView edit(@PathVariable String roleTemplateId) {
        CmsSettings.getInstance().ensureCanManageRoles();
        FenixFramework.atomic(()->((RoleTemplate)FenixFramework.getDomainObject(roleTemplateId)).delete());
        return new RedirectView("/cms/permissions", true);
    }

    private static JsonArray toJsonArray(String json) {
        return new JsonParser().parse(json).getAsJsonArray();
    }

    private List<RoleTemplate> allTemplates() {
        Collection<RoleTemplate> templates = Bennu.getInstance().getRoleTemplatesSet();
        return templates.stream().sorted(Comparator.comparingLong(RoleTemplate::getNumSites)).collect(toList());
    }
}
