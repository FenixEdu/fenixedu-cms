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

import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;
import static pt.ist.fenixframework.FenixFramework.atomic;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.PermissionsArray.Permission;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.CMSComponent;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.component.ComponentDescriptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/components")
public class AdminComponents {

    @RequestMapping(value = "{slugSite}/{slugPage}/create",
        method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> createComponent(@PathVariable String slugSite,
                                                  @PathVariable String slugPage,
                                                  @RequestBody String json) throws Exception {
        Site site = Site.fromSlug(slugSite);
        AdminSites.canEdit(site);

        Page page = site.pageForSlug(slugPage);

        createComponent(page, new JsonParser().parse(json).getAsJsonObject());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Atomic(mode = TxMode.WRITE)
    private void createComponent(Page page, JsonObject json) throws Exception {
        ensureCanDoThis(page.getSite(), Permission.EDIT_PAGE_COMPONENTS);
        String componentType = json.get("type").getAsString();
        ComponentDescriptor descriptor = Component.forType(componentType);
        if (descriptor == null) {
            throw new IllegalArgumentException("Component '" + componentType + "' is unknown!");
        }
        if (descriptor.isStateless()) {
            @SuppressWarnings("unchecked")
            Class<? extends CMSComponent> type = (Class<? extends CMSComponent>) descriptor.getType();
            page.addComponents(Component.forType(type));
        } else {
            JsonObject params = json.get("parameters").getAsJsonObject();
            page.addComponents(descriptor.instantiate(params));
        }
    }

    @ResponseBody
    @RequestMapping(value = "/componentArguments/{page}", produces = "application/json;charset=UTF-8")
    public String getComponentArguments(@PathVariable Page page, @RequestParam String type) {
        ensureCanDoThis(page.getSite(), Permission.SEE_PAGE_COMPONENTS);
        AdminSites.canEdit(page.getSite());
        ComponentDescriptor descriptor = Component.forType(type);
        if (descriptor == null) {
            throw new IllegalArgumentException("Component '" + type + "' is unknown!");
        }
        return descriptor.getParameterDescription(page).toString();
    }

    @RequestMapping(value = "{slugSite}/{slugPage}/{componentId}/delete", method = RequestMethod.POST)
    public RedirectView deleteComponent(@PathVariable String slugSite, @PathVariable String slugPage,
                                        @PathVariable String componentId) {

        Site s = Site.fromSlug(slugSite);
        AdminSites.canEdit(s);
        Page p = s.pageForSlug(slugPage);
        Component component = p.componentForOid(componentId);

        if(component!=null) {
            atomic(() -> {
                ensureCanDoThis(s, Permission.EDIT_PAGE_COMPONENTS, Permission.DELETE_PAGE_COMPONENTS);
                component.removeInstalledPage(p);
                if(component.getInstalledPageSet().isEmpty()) {
                    component.delete();
                }
            });
        }

        return new RedirectView("/cms/pages/advanced/" + s.getSlug() + "/" + p.getSlug() + "/edit", true);
    }
}
