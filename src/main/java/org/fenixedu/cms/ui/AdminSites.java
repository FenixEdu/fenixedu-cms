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

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.Account;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.Webproperties;
import com.google.api.services.analytics.model.Webproperty;
import com.google.common.base.Strings;
import com.google.common.io.Files;

import org.apache.tika.io.FilenameUtils;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.domain.BennuPortalDomainException;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.social.domain.api.GoogleAPI;
import org.fenixedu.bennu.social.domain.user.GoogleUser;
import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.cms.domain.CMSFolder;
import org.fenixedu.cms.domain.CloneCache;
import org.fenixedu.cms.domain.CmsSettings;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.PermissionEvaluation;
import org.fenixedu.cms.domain.PermissionsArray;
import org.fenixedu.cms.domain.PermissionsArray.Permission;
import org.fenixedu.cms.domain.Role;
import org.fenixedu.cms.domain.RoleTemplate;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.SiteActivity;
import org.fenixedu.cms.domain.SiteExporter;
import org.fenixedu.cms.domain.SiteImporter;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.fenixedu.commons.i18n.LocalizedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletResponse;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import static java.util.stream.Collectors.toList;
import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;

@SpringApplication(group = "logged", path = "cms", title = "application.title.cms")
@SpringFunctionality(app = AdminSites.class, title = "application.admin-portal.title")
@RequestMapping("/cms/sites")
public class AdminSites {
    private static final String ZIP_MIME_TYPE = "application/zip";
    private static final String JSON = "application/json;charset=utf-8";
    protected static final Logger LOGGER = LoggerFactory.getLogger(AdminSites.class);
    private static final int ITEMS_PER_PAGE = 10;
    private static final int ITEMS_PER_FOLDER_HOME = 5;
    @RequestMapping
    public String list(Model model,@RequestParam(required = false, defaultValue = "1") int page,
                       @RequestParam(required = false) String query, @RequestParam(required = false) String tag) {
        CMSFolder selectedFolder = null;
        if (tag != null){
            if (tag.equals("â\u009C\u0098") || tag.equals("")){
                model.addAttribute("folder", "no-folder");
            }else{
                selectedFolder = Bennu.getInstance().getCmsFolderSet().stream().filter(x -> x.getFunctionality().getPath().equals(tag)).findAny().orElseThrow(CmsDomainException::notFound);
                model.addAttribute("folder", selectedFolder);
            }
        }

        List<Site> allSites = getSites().stream().sorted(Site.NAME_COMPARATOR).collect(Collectors.toList());

        HashMap<CMSFolder, List<Site>> sitesByFolders = new HashMap<>();
        HashMap<CMSFolder, Integer> folderCount = new HashMap<>();
        List<Site> results = new ArrayList<>();

        for (Site site : allSites) {
            List<Site> list;
            CMSFolder folder = site.getFolder();
            if (!sitesByFolders.containsKey(folder)) {
                list = new ArrayList<>();
                sitesByFolders.put(folder, list);
                folderCount.put(folder, 0);
            } else {
                list = sitesByFolders.get(folder);
            }

            if (list.size() < ITEMS_PER_FOLDER_HOME) {
                list.add(site);
            }

            folderCount.put(folder, folderCount.get(folder) + 1);

            if (!(Strings.isNullOrEmpty(query) && Strings.isNullOrEmpty(tag))) {
                if (tag != null && site.getFolder() != selectedFolder){
                    continue;
                }
                if (query == null || (query != null && SearchUtils.matches(site, query.toLowerCase()))){
                    results.add(site);
                }
            }
        }

        model.addAttribute("foldersSorted", sitesByFolders.keySet().stream().sorted(new Comparator<CMSFolder>() {
            @Override
            public int compare(CMSFolder o1, CMSFolder o2) {
                if (o1 == null) {
                    return Integer.MIN_VALUE;
                }

                if (o2 == null) {
                    return Integer.MAX_VALUE;
                }

                return o1.getFunctionality().getTitle().getContent().compareTo(o2.getFunctionality().getTitle().getContent());
            }
        }).collect(Collectors.toList()));

        model.addAttribute("sitesByFolders", sitesByFolders);
        model.addAttribute("foldersCount", folderCount);

        SearchUtils.Partition<Site> partition = new SearchUtils.Partition<>(results, Site.NAME_COMPARATOR, ITEMS_PER_PAGE, page);
        model.addAttribute("partition", partition);
        model.addAttribute("sites", partition.getItems());
        model.addAttribute("query",query);

        model.addAttribute("folders", Bennu.getInstance().getCmsFolderSet().stream().sorted(Comparator.comparing(x -> x.getFunctionality().getTitle().getContent())).collect(Collectors.toList()));
        model.addAttribute("tag", tag);
        model.addAttribute("roles", Bennu.getInstance().getRoleTemplatesSet().stream().sorted(Comparator.comparing( x -> x.getDescription().getContent())).collect(Collectors.toSet()));

        model.addAttribute("allPermissions",PermissionsArray.all());
        model.addAttribute("cmsSettings", CmsSettings.getInstance());
        model.addAttribute("isManager", DynamicGroup.get("managers").isMember(Authenticate.getUser()));

        return "fenixedu-cms/manage";
    }

    @RequestMapping("/{slug}")
    public String manage(Model model, @PathVariable String slug) throws IOException {
        Site site = Site.fromSlug(slug);
        canEdit(site);
        model.addAttribute("site", site);
        model.addAttribute("cmsSettings", CmsSettings.getInstance());
        return "fenixedu-cms/manageSite";
    }

    @RequestMapping(value = "/{slug}/analytics", method = RequestMethod.GET, produces = JSON)
    public @ResponseBody String viewSiteAnalyticsData(@PathVariable String slug) {
        Site site = Site.fromSlug(slug);
        ensureCanDoThis(site, Permission.MANAGE_ANALYTICS);
        canEdit(site);
        return site.getAnalytics().getOrFetch(site).toString();
    }

    private Analytics getUserAnalytics() {
        GoogleCredential credential =
                GoogleAPI.getInstance().getAuthenticatedUser(Authenticate.getUser()).get().getAuthenticatedSDK();
        return new Analytics.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential).setApplicationName(
                PortalConfiguration.getInstance().getApplicationTitle().getContent(Locale.ENGLISH)).build();
    }

    @RequestMapping(value = "/{slug}/export", method = RequestMethod.GET)
    public void export(@PathVariable String slug, HttpServletResponse response) {
        Site site = Site.fromSlug(slug);
        canEdit(site);
        CmsSettings.getInstance().ensureCanManageSettings();

        try {
            response.setContentType(ZIP_MIME_TYPE);
            response.setHeader("Content-Disposition", "attachment;filename=" + FilenameUtils.normalize(slug + ".zip"));
            new SiteExporter(site).export().writeTo(response.getOutputStream());
            //TODO - show success message
        } catch (IOException e) {
            //TODO - show error message
            throw new RuntimeException("Error exporting site " + slug, e);
        }
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public RedirectView importSite(@RequestParam MultipartFile attachment) {
        CmsSettings.getInstance().ensureCanManageSettings();
        try {
            siteImport(attachment);
            //TODO - show success message
        } catch (Exception e) {
            //TODO - show error message
            throw new RuntimeException("Error importing site ", e);
        }
        return new RedirectView("/cms/sites/", true);
    }

    @Atomic(mode = TxMode.WRITE)
    private Site siteImport(MultipartFile siteZipFile) throws Exception {
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".zip");
        Files.write(siteZipFile.getBytes(), tempFile);
        Site site = new SiteImporter(new ZipFile(tempFile)).importSite();
        SiteActivity.importedSite(site, Authenticate.getUser());
        return site;
    }

    private List<Site> getSites() {
        return getSites(Bennu.getInstance().getSitesSet());
    }

    private List<Site> getSites(Collection<Site> sites){
        User user = Authenticate.getUser();

        return sites.stream().filter(x -> PermissionEvaluation.canAccess(user, x)).sorted(Site.CREATION_DATE_COMPARATOR)
                .collect(toList());
    }

    public static void canEdit(Site site) {
        if (site == null) {
            throw CmsDomainException.notFound();
        }
        // TODO: remove this method
        /*if (!(site.getCanAdminGroup().isMember(Authenticate.getUser()))) {
            throw CmsDomainException.forbiden();
        }*/
    }

    @RequestMapping(value = "{slug}/edit", method = RequestMethod.POST)
    public RedirectView edit(@PathVariable(value = "slug") String slug, @RequestParam LocalizedString name,
            @RequestParam LocalizedString description, @RequestParam(required = false) String theme, @RequestParam(
                    required = false) String newSlug, @RequestParam(required = false, defaultValue = "") String initialPageSlug,
            @RequestParam(required = false, defaultValue = "false") Boolean published,
            @RequestParam(required = false) String viewGroup, @RequestParam(required = false) String folder, @RequestParam(
                    required = false) String analyticsCode, @RequestParam(required = false) String accountId,
            RedirectAttributes redirectAttributes) {

        if (name.isEmpty()) {
            redirectAttributes.addFlashAttribute("emptyName", true);
            return new RedirectView("/sites/" + slug + "/edit", true);
        } else {
            Site s = Site.fromSlug(slug);
            AdminSites.canEdit(s);
            newSlug = Optional.ofNullable(newSlug).orElse(slug);
            editSite(name, description, theme, newSlug, published, s, viewGroup, folder, analyticsCode, accountId,
                    s.pageForSlug(initialPageSlug));
            return new RedirectView("/cms/sites/" + newSlug + "/edit", true);
        }
    }

    @RequestMapping(value = "{slug}/edit", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable(value = "slug") String slug) {
        Site site = Site.fromSlug(slug);
        AdminSites.canEdit(site);

        model.addAttribute("site", site);
        model.addAttribute("themes", Bennu.getInstance().getCMSThemesSet());
        model.addAttribute("folders", Bennu.getInstance().getCmsFolderSet());
        model.addAttribute("defaultSite", Bennu.getInstance().getDefaultSite());
        if (PermissionEvaluation.canDoThis(site, Permission.MANAGE_ANALYTICS)) {
            model.addAttribute("google", GoogleAPI.getInstance());

            GoogleAPI.getInstance().getAuthenticatedUser(Authenticate.getUser()).ifPresent(googleUser -> {
                Analytics analytics = getUserAnalytics();
                List<GoogleAccountBean> googleAccountBeans = new ArrayList<>();
                try {
                    Accounts accounts = analytics.management().accounts().list().execute();
                    for (Account account : accounts.getItems()) {
                        Webproperties properties = analytics.management().webproperties().list(account.getId()).execute();
                        googleAccountBeans.add(new GoogleAccountBean(account, properties));
                    }
                    model.addAttribute("googleUser", googleUser);
                    model.addAttribute("accounts", googleAccountBeans);
                } catch (GoogleJsonResponseException e) {
                    LOGGER.error("Error loading analytics properties", e);
                    if (e.getDetails().getCode() == 401) {
                        //Invalid credentials -> remove invalid user
                    FenixFramework.atomic(() -> googleUser.delete());
                }
            } catch (IOException e) {
                LOGGER.error("Error loading analytics properties", e);
            }

        }   );

        }
        return "fenixedu-cms/editSite";
    }

    @RequestMapping(value = "{siteSlug}/clone", method = RequestMethod.POST)
    public RedirectView clone(@PathVariable String siteSlug) {
        Site s = Site.fromSlug(siteSlug);
        AdminSites.canEdit(s);
        Site newSite = cloneSite(s);
        //TODO - add success message
        return new RedirectView("/cms/sites/", true);
    }

    @Atomic(mode = TxMode.WRITE)
    private Site cloneSite(Site originalSite) {
        Site clone = originalSite.clone(new CloneCache());
        clone.setName(originalSite.getName().append(" (clone)"));
        SiteActivity.clonedSite(clone, Authenticate.getUser());
        return clone;
    }

    @Atomic(mode = TxMode.WRITE)
    private void editSite(LocalizedString name, LocalizedString description, String theme, String slug, Boolean published,
            Site s, String viewGroup, String folder, String analyticsCode, String accountId, Page initialPage) {

        if (PermissionEvaluation.canDoThis(s, Permission.EDIT_SITE_INFORMATION)) {
            s.setName(name);
            s.setDescription(description);
        }

        if (PermissionEvaluation.canDoThis(s, Permission.CHANGE_THEME)) {
            s.setThemeType(Optional.ofNullable(theme).orElseGet(() -> s.getThemeType()));
        }

        if (PermissionEvaluation.canDoThis(s, Permission.CHOOSE_PATH_AND_FOLDER)) {
            slug = Optional.ofNullable(slug).orElseGet(() -> s.getSlug());
            if (!Strings.isNullOrEmpty(folder)) {
                s.setFolder(FenixFramework.getDomainObject(folder));
            } else if (s.getFolder() != null) {
                // Remove the folder and set the new slug, so the MenuFunctionality
                // will be created
                s.setFolder(null);
                s.setSlug(slug);
                s.updateMenuFunctionality();
            }

            if (!s.getSlug().equals(slug)) {
                s.setSlug(slug);
                s.updateMenuFunctionality();
            }
        }

        if (PermissionEvaluation.canDoThis(s, Permission.MANAGE_ANALYTICS)) {
            Optional<GoogleUser> googleUser = GoogleAPI.getInstance().getAuthenticatedUser(Authenticate.getUser());
            if ((Strings.isNullOrEmpty(analyticsCode) || Strings.isNullOrEmpty(accountId)) && googleUser.isPresent()) {
                googleUser.get().delete();
            }
            s.setAnalyticsCode(analyticsCode);
            s.setAnalyticsAccountId(accountId);
            s.getAnalytics().update(s);
        }

        if (PermissionEvaluation.canDoThis(s, Permission.PUBLISH_SITE)) {
            s.setPublished(published);
            s.setCanViewGroup(Group.parse(viewGroup));
        }

        if (PermissionEvaluation.canDoThis(s, Permission.CHOOSE_DEFAULT_PAGE)) {
            s.setInitialPage(initialPage);
        }

    }

    @RequestMapping(value = "{slug}/delete", method = RequestMethod.POST)
    public RedirectView delete(@PathVariable(value = "slug") String slug) {
        Site s = Site.fromSlug(slug);

        AdminSites.canEdit(s);

        s.delete();
        return new RedirectView("/cms/sites", true);
    }

    @RequestMapping(value = "cmsSettings", method = RequestMethod.POST)
    public RedirectView editSettings(@RequestParam String slug, @RequestParam(required = false) String themesManagers, @RequestParam(required = false) String rolesManagers,
                                     @RequestParam(required = false) String foldersManagers, @RequestParam(required = false) String settingsManagers) {
        FenixFramework.atomic(() -> {
            if (Bennu.getInstance().getDefaultSite() == null || !Bennu.getInstance()
                .getDefaultSite().getSlug().equals(slug)) {
                Site s = Site.fromSlug(slug);

                CmsSettings.getInstance().ensureCanManageSettings();

                Bennu.getInstance().setDefaultSite(s);
            }
            if (DynamicGroup.get("managers").isMember(Authenticate.getUser())) {
                CmsSettings settings = CmsSettings.getInstance().getInstance();
                settings.ensureCanManageGlobalPermissions();
                settings.setThemesManagers(group(themesManagers));
                settings.setRolesManagers(group(rolesManagers));
                settings.setFoldersManagers(group(foldersManagers));
                settings.setSettingsManagers(group(settingsManagers));
            }
        });
        return new RedirectView("/cms/sites", true);
    }

    private static PersistentGroup group(String expression) {
        Group group = Group.parse(expression);
        if(!group.isMember(Authenticate.getUser())) {
            CmsDomainException.forbiden();
        }
        return group.toPersistentGroup();
    }

    @RequestMapping(value = "/{slugSite}/roles", method = RequestMethod.GET)
    public String viewSiteRoles(@PathVariable String slugSite, Model model) {
        Site site = Site.fromSlug(slugSite);
        ensureCanDoThis(site, Permission.MANAGE_ROLES);
        Set<RoleTemplate> siteTemplates = site.getRolesSet().stream().map(Role::getRoleTemplate).collect(Collectors.toSet());
        model.addAttribute("site", site);
        model.addAttribute("roles",
                site.getRolesSet().stream().sorted(Comparator.comparing(Role::getName)).collect(Collectors.toList()));
        return "fenixedu-cms/editSiteRoles";
    }

    @RequestMapping(value = "/{slugSite}/roles/{roleId}/edit", method = RequestMethod.GET)
    public String viewEditRole(@PathVariable String slugSite, @PathVariable String roleId, Model model) {
        PermissionEvaluation.canAccess(Authenticate.getUser(), Site.fromSlug(slugSite));
        model.addAttribute("role", FenixFramework.getDomainObject(roleId));
        return "fenixedu-cms/editRole";
    }

    @RequestMapping(value = "/{slugSite}/roles/{roleId}/edit", method = RequestMethod.POST)
    public RedirectView editSiteRole(@PathVariable String slugSite, @PathVariable String roleId, @RequestParam String group) {
        FenixFramework.atomic(() -> {
            Site site = Site.fromSlug(slugSite);
            PermissionEvaluation.canDoThis(site, Permission.MANAGE_ROLES);
            Role role = FenixFramework.getDomainObject(roleId);
            role.setGroup(Group.parse(group).toPersistentGroup());
        });
        return new RedirectView("/cms/sites/" + slugSite + "/roles/" + roleId + "/edit", true);
    }

    @RequestMapping(value = "/{slugSite}/roles/add", method = RequestMethod.POST)
    public RedirectView createSiteRole(@PathVariable String slugSite, @RequestParam String roleTemplateId) {
        FenixFramework.atomic(() -> {
            Site site = Site.fromSlug(slugSite);
            ensureCanDoThis(site, Permission.MANAGE_ROLES);
            RoleTemplate template = FenixFramework.getDomainObject(roleTemplateId);
            if (!template.getRolesSet().stream().map(Role::getSite).filter(roleSite -> roleSite.equals(site)).findAny()
                    .isPresent()) {
                new Role(template, site);
            }
        });
        return new RedirectView("/cms/sites/" + slugSite + "/roles/" + roleTemplateId + "/edit", true);
    }

    @RequestMapping(value = "/{slugSite}/roles/{roleId}/delete", method = RequestMethod.POST)
    public RedirectView removeRole(@PathVariable String slugSite, @PathVariable String roleId) {
        CmsSettings.getInstance().ensureCanManageRoles();
        FenixFramework.atomic(() -> ((Role) FenixFramework.getDomainObject(roleId)).delete());
        return new RedirectView("/cms/sites/" + slugSite + "/roles", true);
    }

    public static class GoogleAccountBean {
        private final Account account;
        private final Webproperties properties;

        public GoogleAccountBean(Account account, Webproperties properties) {
            this.account = account;
            this.properties = properties;
        }

        public String getName() {
            return account.getName();
        }

        public String getId() {
            return account.getId();
        }

        public List<GoogleAccountProperty> getProperties() {
            return properties.getItems().stream().map(GoogleAccountProperty::new).collect(toList());
        }
    }

    public static class GoogleAccountProperty {
        private final Webproperty webproperty;

        public GoogleAccountProperty(Webproperty webproperty) {
            this.webproperty = webproperty;
        }

        public String getName() {
            return webproperty.getName();
        }

        public String getId() {
            return webproperty.getId();
        }
    }

}