package org.fenixedu.cms.ui;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by diutsu on 23/01/17.
 */
@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/builders")
public class AdminSiteBuilder {
    
    @RequestMapping
    public String list(Model model) {
        CmsSettings.getInstance().ensureCanManageSettings();
        model.addAttribute("siteBuilders",Bennu.getInstance().getSiteBuildersSet());
        model.addAttribute("cmsSettings", CmsSettings.getInstance());
        return "fenixedu-cms/manageSiteBuilders";
    }
    
    @RequestMapping(value = "/{builderSlug}", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable("builderSlug") String builderSlug) {
        CmsSettings.getInstance().ensureCanManageSettings();
        model.addAttribute("builder", SiteBuilder.forSlug(builderSlug));
        model.addAttribute("roles", Bennu.getInstance().getRoleTemplatesSet().stream().sorted(Comparator.comparing(x -> x.getName()
                .getContent())).collect(Collectors.toSet()));
        model.addAttribute("folders", Bennu.getInstance().getCmsFolderSet().stream().sorted(Comparator.comparing(x -> x.getFunctionality().getTitle().getContent())).collect(Collectors.toList()));
        model.addAttribute("allPermissions", PermissionsArray.all());
        model.addAttribute("cmsSettings", CmsSettings.getInstance());
        model.addAttribute("themes", Bennu.getInstance().getCMSThemesSet().stream()
                .sorted(Comparator.comparing(CMSTheme::getName)).collect(Collectors.toList()));
        return "fenixedu-cms/editSiteBuilder";
    }
    
    
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String create(Model model, @RequestParam String builderSlug) {
        CmsSettings.getInstance().ensureCanManageSettings();
        SiteBuilder builder = createBuilder(builderSlug);
        return "redirect:/cms/"+builder.getSlug();
    }
    
    @Atomic(mode= Atomic.TxMode.WRITE)
    private SiteBuilder createBuilder(String builderSlug) {
        return new SiteBuilder(builderSlug);
    }
    
    @RequestMapping(value = "/{builderSlug}", method = RequestMethod.POST)
    public String update(Model model, @PathVariable("builderSlug") String builderSlug,
                         @RequestParam(required = false)  String canViewGroup, @RequestParam(required = false) String newSlug,
                         @RequestParam(required = false)  String theme, @RequestParam(required = false)  String folder,
                         @RequestParam(required = false) String defaultRole, @RequestParam(required = false) Set<String> roles ) {
        CmsSettings.getInstance().ensureCanManageSettings();
        ArrayList<String> errors = new ArrayList<>();
        SiteBuilder builder = SiteBuilder.forSlug(builderSlug);
    
        if (builder != null){
            FenixFramework.atomic(() -> {
                builder.setSlug(newSlug);
                builder.setTheme(CMSTheme.forType(theme));
                builder.setFolder(FenixFramework.getDomainObject(folder));
                builder.getRoleTemplateSet().forEach(rt -> builder.removeRoleTemplate(rt));
                if (builder.isSystemBuilder()) {
                    ((SystemSiteBuilder) builder).setDefaultRoleTemplate(FenixFramework.getDomainObject(defaultRole));
                }
                if (roles != null) {
                    roles.stream().map(r -> (RoleTemplate) FenixFramework.getDomainObject(r))
                            .filter(rt -> rt != null)
                            .forEach(rt -> builder.addRoleTemplate(rt));
                }
                builder.setCanViewGroup(Group.parse(canViewGroup));
            });
        } else {
            errors.add("error.invalid.slug");
        }
        
        if(!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "fenixedu-cms/editSiteBuilder";
        } else {
            return "redirect:/cms/builders";
        }
        
    }
    
    
}
