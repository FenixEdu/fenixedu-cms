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


import com.google.common.base.Strings;
import com.google.common.io.Files;

import org.apache.tika.io.FilenameUtils;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.social.domain.api.GoogleAPI;
import org.fenixedu.bennu.social.domain.api.GoogleAPI_Base;
import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.cms.domain.*;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.fenixedu.cms.exceptions.ResourceNotFoundException;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

@SpringApplication(group = "logged", path = "cms", title = "application.title.cms")
@SpringFunctionality(app = AdminSites.class, title = "application.admin-portal.title")
@RequestMapping("/cms/sites")
public class AdminSites {

    private static final int ITEMS_PER_PAGE = 10;
    private static final String ZIP_MIME_TYPE = "application/zip";

    @RequestMapping
    public String list(Model model, @RequestParam(required = false, defaultValue = "1") int page,
                    @RequestParam(required = false) String query) {
        List<Site> allSites = Strings.isNullOrEmpty(query) ? getSites() : SearchUtils.searchSites(getSites(), query);
        SearchUtils.Partition<Site> partition = new SearchUtils.Partition<>(allSites, Site.NAME_COMPARATOR, ITEMS_PER_PAGE, page);
        model.addAttribute("partition", partition);
        model.addAttribute("sites", partition.getItems());
        model.addAttribute("isManager", DynamicGroup.get("managers").isMember(Authenticate.getUser()));
        model.addAttribute("query", query);

        String redirectUrl = CoreConfiguration.getConfiguration().applicationUrl() + "/cms/sites/google/oauth2callback";
        model.addAttribute("googleAuthJSOrigin", CoreConfiguration.getConfiguration().applicationUrl());
        model.addAttribute("googleRedirectUrl", redirectUrl);


        return "fenixedu-cms/manage";
    }

    @RequestMapping("/{slug}")
    public String manage(Model model, @PathVariable String slug) throws IOException {
        Site site = Site.fromSlug(slug);
        canEdit(site);

        model.addAttribute("site", site);
        GoogleAPI google = GoogleAPI.getInstance();
        
        model.addAttribute(
                    "views",
                    "{\"20150419\":{\"pageviews\":\"13\",\"visitors\":\"9\"},\"20150420\":{\"pageviews\":\"8\",\"visitors\":\"5\"},\"20150421\":{\"pageviews\":\"17\",\"visitors\":\"10\"},\"20150422\":{\"pageviews\":\"13\",\"visitors\":\"12\"},\"20150423\":{\"pageviews\":\"7\",\"visitors\":\"7\"},\"20150424\":{\"pageviews\":\"5\",\"visitors\":\"5\"},\"20150425\":{\"pageviews\":\"6\",\"visitors\":\"5\"},\"20150426\":{\"pageviews\":\"11\",\"visitors\":\"10\"},\"20150427\":{\"pageviews\":\"7\",\"visitors\":\"6\"},\"20150428\":{\"pageviews\":\"8\",\"visitors\":\"6\"},\"20150429\":{\"pageviews\":\"15\",\"visitors\":\"13\"},\"20150430\":{\"pageviews\":\"9\",\"visitors\":\"8\"},\"20150501\":{\"pageviews\":\"5\",\"visitors\":\"3\"},\"20150502\":{\"pageviews\":\"8\",\"visitors\":\"8\"},\"20150503\":{\"pageviews\":\"4\",\"visitors\":\"4\"},\"20150504\":{\"pageviews\":\"16\",\"visitors\":\"10\"},\"20150505\":{\"pageviews\":\"16\",\"visitors\":\"13\"},\"20150506\":{\"pageviews\":\"5\",\"visitors\":\"4\"},\"20150507\":{\"pageviews\":\"14\",\"visitors\":\"11\"},\"20150508\":{\"pageviews\":\"10\",\"visitors\":\"9\"},\"20150509\":{\"pageviews\":\"5\",\"visitors\":\"5\"},\"20150510\":{\"pageviews\":\"11\",\"visitors\":\"9\"},\"20150511\":{\"pageviews\":\"19\",\"visitors\":\"15\"},\"20150512\":{\"pageviews\":\"6\",\"visitors\":\"6\"},\"20150513\":{\"pageviews\":\"17\",\"visitors\":\"6\"},\"20150514\":{\"pageviews\":\"16\",\"visitors\":\"12\"},\"20150515\":{\"pageviews\":\"23\",\"visitors\":\"18\"},\"20150516\":{\"pageviews\":\"10\",\"visitors\":\"7\"},\"20150517\":{\"pageviews\":\"7\",\"visitors\":\"7\"},\"20150518\":{\"pageviews\":\"4\",\"visitors\":\"4\"},\"20150519\":{\"pageviews\":\"9\",\"visitors\":\"6\"}}");


        return "fenixedu-cms/manageSite";
    }

    @RequestMapping(value = "/{slug}/export", method = RequestMethod.GET)
    public void export(@PathVariable String slug, HttpServletResponse response) {
        Site site = Site.fromSlug(slug);
        canEdit(site);
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
        User user = Authenticate.getUser();
        Set<Site> allSites = Bennu.getInstance().getSitesSet();

        return allSites.stream().filter(x -> PermissionEvaluation.canAccess(user, x)).sorted(Site.NAME_COMPARATOR).collect(Collectors.toList());
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

    @RequestMapping(value = "{slug}/edit", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable(value = "slug") String slug) {
        Site site = Site.fromSlug(slug);
        try {

            AdminSites.canEdit(site);

            model.addAttribute("site", site);
            model.addAttribute("themes", Bennu.getInstance().getCMSThemesSet());
            model.addAttribute("folders", Bennu.getInstance().getCmsFolderSet());
            model.addAttribute("defaultSite", Bennu.getInstance().getDefaultSite());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "fenixedu-cms/editSite";
    }

    @RequestMapping(value = "{slug}/edit", method = RequestMethod.POST)
    public RedirectView edit(@PathVariable(value = "slug") String slug, @RequestParam LocalizedString name,
                             @RequestParam LocalizedString description, @RequestParam String theme, @RequestParam String newSlug, @RequestParam(
            required = false, defaultValue = "false") Boolean published, RedirectAttributes redirectAttributes, @RequestParam String viewGroup,
                             @RequestParam String postGroup, @RequestParam String adminGroup, @RequestParam String folder,
                             @RequestParam String analyticsCode, @RequestParam(required = false) String accountId, @RequestParam String initialPageSlug) {

        if (name.isEmpty()) {
            redirectAttributes.addFlashAttribute("emptyName", true);
            return new RedirectView("/sites/" + slug + "/edit", true);
        } else {
            Site s = Site.fromSlug(slug);
            AdminSites.canEdit(s);
            CMSTheme themeObj = Optional.ofNullable(CMSTheme.forType(theme)).orElseThrow(ResourceNotFoundException::new);

            editSite(name, description, themeObj, newSlug, published, s, viewGroup, postGroup, adminGroup, folder, analyticsCode, accountId, s.pageForSlug(initialPageSlug));
            return new RedirectView("/cms/sites/" + newSlug + "/edit", true);
        }
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
    private void editSite(LocalizedString name, LocalizedString description, CMSTheme themeObj, String slug, Boolean published,
                          Site s, String viewGroup, String postGroup, String adminGroup, String folder, String analyticsCode, String accountId, Page initialPage) {

        s.setName(name);
        s.setDescription(description);

        s.setThemeType(themeObj.getType());
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

        s.setAnalyticsCode(analyticsCode);


        s.setPublished(published);
        s.setInitialPage(initialPage);
    }

    @RequestMapping(value = "{slug}/delete", method = RequestMethod.POST)
    public RedirectView delete(@PathVariable(value = "slug") String slug) {
        Site s = Site.fromSlug(slug);

        AdminSites.canEdit(s);

        s.delete();
        return new RedirectView("/cms/sites", true);
    }

    @RequestMapping(value = "cmsSettings", method = RequestMethod.POST)
    public RedirectView view(@RequestParam String slug, @RequestParam(required = false) String googleClientId, @RequestParam(
            required = false) String googleClientSecret) throws IOException {

        saveSettings(slug, googleClientId, googleClientSecret);

        return new RedirectView("/cms/sites", true);
    }

    @Atomic
    private void saveSettings(String slug, String clientId, String clientSecret) throws IOException {

        if (Bennu.getInstance().getDefaultSite() == null || !Bennu.getInstance().getDefaultSite().getSlug().equals(slug)) {
            Site s = Site.fromSlug(slug);

            if (!DynamicGroup.get("managers").isMember(Authenticate.getUser())) {
                throw CmsDomainException.forbiden();
            }

            Bennu.getInstance().setDefaultSite(s);
        }
    }


}