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
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.Profile;
import com.google.api.services.analytics.model.Profiles;
import com.google.api.services.analytics.model.Webproperties;
import com.google.api.services.analytics.model.Webproperty;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.tika.io.FilenameUtils;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.social.domain.api.GoogleAPI;
import org.fenixedu.bennu.social.domain.user.GoogleUser;
import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.cms.domain.CMSTheme;
import org.fenixedu.cms.domain.CloneCache;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.PermissionEvaluation;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.SiteActivity;
import org.fenixedu.cms.domain.SiteExporter;
import org.fenixedu.cms.domain.SiteImporter;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.fenixedu.cms.exceptions.ResourceNotFoundException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletResponse;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

import static java.util.stream.Collectors.toList;

@SpringApplication(group = "logged", path = "cms", title = "application.title.cms")
@SpringFunctionality(app = AdminSites.class, title = "application.admin-portal.title")
@RequestMapping("/cms/sites")
public class AdminSites {
    private static final String JSON = "application/json;charset=utf-8";
    protected static final Logger LOGGER = LoggerFactory.getLogger(AdminSites.class);
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

        return "fenixedu-cms/manage";
    }

    @RequestMapping("/{slug}")
    public String manage(Model model, @PathVariable String slug) throws IOException {
        Site site = Site.fromSlug(slug);
        canEdit(site);
        model.addAttribute("site", site);
        return "fenixedu-cms/manageSite";
    }

    private boolean hasGoogle(Site site, User user) {
        return GoogleAPI.getInstance().isConfigured()
                && GoogleAPI.getInstance().isUserAuthenticated(user)
                && !Strings.isNullOrEmpty(site.getAnalyticsAccountId())
                && !Strings.isNullOrEmpty(site.getAnalyticsCode());
    }

    @RequestMapping(value = "/{slug}/analytics", method = RequestMethod.GET, produces = JSON)
    public @ResponseBody String viewSiteAnalyticsData(@PathVariable String slug) {
        Site site = Site.fromSlug(slug);
        canEdit(site);
        return getSiteAnalytics(site).toString();
    }

    private JsonElement getSiteAnalytics(Site site) {
        try {
            if (!Strings.isNullOrEmpty(site.getAnalyticsAccountId()) && !Strings.isNullOrEmpty(
                site.getAnalyticsCode())) {
                Analytics analytics = getUserAnalytics();
                Profiles profiles = analytics.management().profiles().list(
                    site.getAnalyticsAccountId(), site.getAnalyticsCode()).execute();
                JsonObject db = new JsonObject();
                for (Profile profile : profiles.getItems()) {
                    GaData query = analytics.data().ga()
                            .get("ga:" + profile.getId(), "30daysAgo", "today",
                                 "ga:pageviews,ga:visitors")
                            .setDimensions("ga:date")
                            .execute();

                    for (List<String> days : query.getRows()) {
                        JsonObject o;
                        if (db.has(days.get(0))) {
                            o = (JsonObject) db.get(days.get(0));
                            o.addProperty("pageviews", o.get("pageviews") + days.get(1));
                            o.addProperty("visitors", o.get("visitors") + days.get(2));
                        } else {
                            o = new JsonObject();
                            db.add(days.get(0), o);
                        }
                        o.addProperty("pageviews", days.get(1));
                        o.addProperty("visitors", days.get(2));
                    }
                }
                return db;
            }
        } catch(Exception e) {
            LOGGER.error("Error loading analytics data for site '" + site.getSlug() + "'", e);
        }
        return new JsonObject();
    }

    private Analytics getUserAnalytics() {
        GoogleCredential credential = GoogleAPI.getInstance().getAuthenticatedUser(Authenticate.getUser()).get().getAuthenticatedSDK();
        return new Analytics.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential).setApplicationName(
                PortalConfiguration.getInstance().getApplicationTitle().getContent(Locale.ENGLISH)).build();
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

        return allSites.stream().filter(x -> PermissionEvaluation.canAccess(user, x)).sorted(
            Site.NAME_COMPARATOR).collect(toList());
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
                             @RequestParam LocalizedString description, @RequestParam String theme,
                             @RequestParam String newSlug, @RequestParam String initialPageSlug,
                             @RequestParam(required = false, defaultValue = "false") Boolean published,
                             @RequestParam String viewGroup, @RequestParam String folder,
                             @RequestParam(required = false) String analyticsCode,
                             @RequestParam(required = false) String accountId,
                             RedirectAttributes redirectAttributes) {

        if (name.isEmpty()) {
            redirectAttributes.addFlashAttribute("emptyName", true);
            return new RedirectView("/sites/" + slug + "/edit", true);
        } else {
            Site s = Site.fromSlug(slug);
            AdminSites.canEdit(s);
            CMSTheme themeObj = Optional.ofNullable(CMSTheme.forType(theme)).orElseThrow(ResourceNotFoundException::new);

            editSite(name, description, themeObj, newSlug, published, s, viewGroup, folder, analyticsCode, accountId, s.pageForSlug(initialPageSlug));
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
        model.addAttribute("google", GoogleAPI.getInstance());

        GoogleAPI.getInstance().getAuthenticatedUser(Authenticate.getUser()).ifPresent(
            googleUser -> {
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
              } catch(GoogleJsonResponseException e){
                LOGGER.error("Error loading analytics properties", e);
                if(e.getDetails().getCode() == 401) {
                  //Invalid credentials -> remove invalid user
                  FenixFramework.atomic(()->googleUser.delete());
                }
              }catch (IOException e) {
                LOGGER.error("Error loading analytics properties", e);
              }

            });

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
    private void editSite(LocalizedString name, LocalizedString description, CMSTheme themeObj, String slug, Boolean published,
                          Site s, String viewGroup, String folder, String analyticsCode, String accountId, Page initialPage) {

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


        Optional<GoogleUser> googleUser = GoogleAPI.getInstance().getAuthenticatedUser(Authenticate.getUser());
        if((Strings.isNullOrEmpty(analyticsCode) || Strings.isNullOrEmpty(accountId)) && googleUser.isPresent()) {
            googleUser.get().delete();
        }

        s.setAnalyticsCode(analyticsCode);
        s.setAnalyticsAccountId(accountId);

        s.setPublished(published);
        s.setInitialPage(initialPage);

        s.setCanViewGroup(Group.parse(viewGroup));
    }

    @RequestMapping(value = "{slug}/delete", method = RequestMethod.POST)
    public RedirectView delete(@PathVariable(value = "slug") String slug) {
        Site s = Site.fromSlug(slug);

        AdminSites.canEdit(s);

        s.delete();
        return new RedirectView("/cms/sites", true);
    }

    @RequestMapping(value = "cmsSettings", method = RequestMethod.POST)
    public RedirectView view(@RequestParam String slug) {
        FenixFramework.atomic(()->{
          if (Bennu.getInstance().getDefaultSite() == null || !Bennu.getInstance().getDefaultSite().getSlug().equals(slug)) {
            Site s = Site.fromSlug(slug);

            if (!DynamicGroup.get("managers").isMember(Authenticate.getUser())) {
              throw CmsDomainException.forbiden();
            }

            Bennu.getInstance().setDefaultSite(s);
          }
        });
        return new RedirectView("/cms/sites", true);
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