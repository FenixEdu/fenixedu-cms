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

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.cms.domain.CMSTheme;
import org.fenixedu.cms.domain.GoogleAPIConnection;
import org.fenixedu.cms.domain.GoogleAPIService;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.fenixedu.cms.exceptions.ResourceNotFoundException;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.LocalDate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.Account;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.Profile;
import com.google.api.services.analytics.model.Profiles;
import com.google.api.services.analytics.model.Webproperties;
import com.google.api.services.analytics.model.Webproperty;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@SpringApplication(group = "logged", path = "cms", title = "application.title.cms")
@SpringFunctionality(app = AdminSites.class, title = "application.admin-portal.title")
@RequestMapping("/cms/sites")
public class AdminSites {

    private static final int ITEMS_PER_PAGE = 30;

    @RequestMapping
    public String list(Model model, @RequestParam(required = false) String query) {
        return list(0, model, query);
    }

    @RequestMapping("/{slug}")
    public String manage(Model model, @PathVariable String slug) throws IOException {
        Site site = Site.fromSlug(slug);
        canEdit(site);
        model.addAttribute("site", site);

        if (site.getGoogleAPIConnection() != null && !Strings.isNullOrEmpty(site.getGoogleAPIConnection().getAccountId())
                && !Strings.isNullOrEmpty(site.getAnalyticsCode())) {
//            Analytics analytics = site.getGoogleAPIConnection().getAnalytics();
//
//            Profiles profiles =
//                    analytics.management().profiles().list(site.getGoogleAPIConnection().getAccountId(), site.getAnalyticsCode())
//                            .execute();
//            JsonObject db = new JsonObject();
//            for (Profile profile : profiles.getItems()) {
//                GaData query =
//                        analytics.data().ga().get("ga:" + profile.getId(), "30daysAgo", "today", "ga:pageviews,ga:visitors")
//                                .setDimensions("ga:date").execute();
//                for (List<String> days : query.getRows()) {
//                    JsonObject o;
//                    if (db.has(days.get(0))) {
//                        o = (JsonObject) db.get(days.get(0));
//                        o.addProperty("pageviews", o.get("pageviews") + days.get(1));
//                        o.addProperty("visitors", o.get("visitors") + days.get(2));
//                    } else {
//                        o = new JsonObject();
//                        db.add(days.get(0), o);
//                    }
//                    o.addProperty("pageviews", days.get(1));
//                    o.addProperty("visitors", days.get(2));
//                }
//            }

            model.addAttribute(
                    "views",
                    "{\"20150419\":{\"pageviews\":\"13\",\"visitors\":\"9\"},\"20150420\":{\"pageviews\":\"8\",\"visitors\":\"5\"},\"20150421\":{\"pageviews\":\"17\",\"visitors\":\"10\"},\"20150422\":{\"pageviews\":\"13\",\"visitors\":\"12\"},\"20150423\":{\"pageviews\":\"7\",\"visitors\":\"7\"},\"20150424\":{\"pageviews\":\"5\",\"visitors\":\"5\"},\"20150425\":{\"pageviews\":\"6\",\"visitors\":\"5\"},\"20150426\":{\"pageviews\":\"11\",\"visitors\":\"10\"},\"20150427\":{\"pageviews\":\"7\",\"visitors\":\"6\"},\"20150428\":{\"pageviews\":\"8\",\"visitors\":\"6\"},\"20150429\":{\"pageviews\":\"15\",\"visitors\":\"13\"},\"20150430\":{\"pageviews\":\"9\",\"visitors\":\"8\"},\"20150501\":{\"pageviews\":\"5\",\"visitors\":\"3\"},\"20150502\":{\"pageviews\":\"8\",\"visitors\":\"8\"},\"20150503\":{\"pageviews\":\"4\",\"visitors\":\"4\"},\"20150504\":{\"pageviews\":\"16\",\"visitors\":\"10\"},\"20150505\":{\"pageviews\":\"16\",\"visitors\":\"13\"},\"20150506\":{\"pageviews\":\"5\",\"visitors\":\"4\"},\"20150507\":{\"pageviews\":\"14\",\"visitors\":\"11\"},\"20150508\":{\"pageviews\":\"10\",\"visitors\":\"9\"},\"20150509\":{\"pageviews\":\"5\",\"visitors\":\"5\"},\"20150510\":{\"pageviews\":\"11\",\"visitors\":\"9\"},\"20150511\":{\"pageviews\":\"19\",\"visitors\":\"15\"},\"20150512\":{\"pageviews\":\"6\",\"visitors\":\"6\"},\"20150513\":{\"pageviews\":\"17\",\"visitors\":\"6\"},\"20150514\":{\"pageviews\":\"16\",\"visitors\":\"12\"},\"20150515\":{\"pageviews\":\"23\",\"visitors\":\"18\"},\"20150516\":{\"pageviews\":\"10\",\"visitors\":\"7\"},\"20150517\":{\"pageviews\":\"7\",\"visitors\":\"7\"},\"20150518\":{\"pageviews\":\"4\",\"visitors\":\"4\"},\"20150519\":{\"pageviews\":\"9\",\"visitors\":\"6\"}}");
        }

        return "fenixedu-cms/manageSite";
    }

    @RequestMapping(value = "manage/{page}", method = RequestMethod.GET)
    public String list(@PathVariable(value = "page") int page, Model model, @RequestParam(required = false) String query) {
        List<Site> allSites = Strings.isNullOrEmpty(query) ? getSites() : SearchUtils.searchSites(getSites(), query);
        List<List<Site>> pages = Lists.partition(allSites, ITEMS_PER_PAGE);
        int currentPage = normalize(page, pages);
        model.addAttribute("numberOfPages", pages.size());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("sites", pages.isEmpty() ? Collections.emptyList() : pages.get(currentPage));
        model.addAttribute("isManager", DynamicGroup.get("managers").isMember(Authenticate.getUser()));
        model.addAttribute("query", query);

        String redirectUrl = CoreConfiguration.getConfiguration().applicationUrl() + "/cms/sites/google/oauth2callback";
        model.addAttribute("googleAuthJSOrigin", CoreConfiguration.getConfiguration().applicationUrl());
        model.addAttribute("googleRedirectUrl", redirectUrl);

        model.addAttribute("google", Bennu.getInstance().getGoogleAPISerivce());

        return "fenixedu-cms/manage";
    }

    private int normalize(int page, List<List<Site>> pages) {
        if (page < 0) {
            return 0;
        }
        if (page >= pages.size()) {
            return pages.size() - 1;
        }
        return page;
    }

    private List<Site> getSites() {
        User user = Authenticate.getUser();
        Set<Site> allSites = Bennu.getInstance().getSitesSet();
        Predicate<Site> isAdminMember = site -> site.getCanAdminGroup().isMember(user);
        Predicate<Site> isPostsMember = site -> site.getCanPostGroup().isMember(user);
        return allSites.stream().filter(isAdminMember.or(isPostsMember)).collect(Collectors.toList());
    }

    public static void canEdit(Site site) {
        if (site == null) {
            throw CmsDomainException.notFound();
        }
        if (!(site.getCanAdminGroup().isMember(Authenticate.getUser()))) {
            throw CmsDomainException.forbiden();
        }
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
            GoogleAPIService googleAPISerivce = Bennu.getInstance().getGoogleAPISerivce();
            model.addAttribute("google", googleAPISerivce);

            GoogleAuthorizationCodeFlow google = googleAPISerivce.makeFlow();

            String redirectUrl = CoreConfiguration.getConfiguration().applicationUrl() + "/cms/sites/google/oauth2callback";

            JsonObject obj = new JsonObject();

            obj.addProperty("site", site.getSlug());

            String url =
                    google.newAuthorizationUrl().setState(Base64.getEncoder().encodeToString(obj.toString().getBytes()))
                            .setRedirectUri(redirectUrl).build();

            model.addAttribute("url", url);
            GoogleAPIConnection connection = site.getGoogleAPIConnection();
            if (connection != null) {
                model.addAttribute("googleConnection", connection);
                model.addAttribute("url", CoreConfiguration.getConfiguration().applicationUrl());
                Analytics analytics = connection.getAnalytics();
                Accounts accounts = analytics.management().accounts().list().execute();
                JsonArray arr = new JsonArray();

                for (Account account : accounts.getItems()) {
                    JsonObject o = new JsonObject();

                    o.addProperty("name", account.getName());
                    o.addProperty("accountId", account.getId());

                    Webproperties properties = analytics.management().webproperties().list(account.getId()).execute();
                    JsonArray arr2 = new JsonArray();
                    for (Webproperty property : properties.getItems()) {
                        JsonObject o2 = new JsonObject();

                        o2.addProperty("name", property.getName());
                        o2.addProperty("id", property.getId());
                        arr2.add(o2);
                    }

                    o.add("properties", arr2);
                    arr.add(o);
                }
                model.addAttribute("accounts", arr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "fenixedu-cms/editSite";
    }

    @RequestMapping(value = "google/oauth2callback")
    public RedirectView acceptGoogle(@RequestParam(required = false) String error, String state,
            @RequestParam(required = false) String code) throws IOException {
        if (error == null) {
            GoogleAuthorizationCodeFlow google = Bennu.getInstance().getGoogleAPISerivce().makeFlow();

            String redirectUrl = CoreConfiguration.getConfiguration().applicationUrl() + "/cms/sites/google/oauth2callback";

            GoogleTokenResponse execute = google.newTokenRequest(code).setRedirectUri(redirectUrl).execute();

            String site =
                    new JsonParser().parse(new String(Base64.getDecoder().decode(state))).getAsJsonObject().get("site")
                            .getAsString();

            createToken(site, execute);

        }

        return new RedirectView("/cms/sites/"
                + new JsonParser().parse(new String(Base64.getDecoder().decode(state))).getAsJsonObject().get("site")
                        .getAsString() + "/edit");
    }

    @Atomic
    private void createToken(String site, GoogleTokenResponse execute) {
        GoogleAPIConnection connection = new GoogleAPIConnection();
        connection.setAccessToken(execute.getAccessToken());
        connection.setRefreshToken(execute.getRefreshToken());

        connection.setService(Bennu.getInstance().getGoogleAPISerivce());
        connection.setSite(Site.fromSlug(site));

    }

    @RequestMapping(value = "{slug}/createGoogleProperty", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public String createGoogleProperty(@PathVariable(value = "slug") String slug, @RequestParam String name,
            @RequestParam String url, @RequestParam String account) throws IOException {
        Site s = Site.fromSlug(slug);

        AdminSites.canEdit(s);

        Webproperty body = new Webproperty();
        body.setWebsiteUrl(url);
        body.setName(name);
        Webproperty result;
        try {
            result = s.getGoogleAPIConnection().getAnalytics().management().webproperties().insert(account, body).execute();

            FenixFramework.atomic(() -> {
                s.setAnalyticsCode(result.getId());
            });

            JsonObject obj = new JsonObject();

            obj.addProperty("account", account);
            obj.addProperty("id", result.getId());

            return obj.toString();
        } catch (GoogleJsonResponseException e) {
            JsonObject obj = new JsonObject();

            obj.addProperty("code", e.getDetails().getCode());
            obj.addProperty("message", e.getDetails().getMessage());

            return obj.toString();
        }

    }

    @RequestMapping(value = "{slug}/edit", method = RequestMethod.POST)
    public RedirectView edit(Model model, @PathVariable(value = "slug") String slug, @RequestParam LocalizedString name,
            @RequestParam LocalizedString description, @RequestParam String theme, @RequestParam String newSlug, @RequestParam(
                    required = false) Boolean published, RedirectAttributes redirectAttributes, @RequestParam String viewGroup,
            @RequestParam String postGroup, @RequestParam String adminGroup, @RequestParam String folder,
            @RequestParam String analyticsCode, @RequestParam(required = false) String accountId) {

        if (name.isEmpty()) {
            redirectAttributes.addFlashAttribute("emptyName", true);
            return new RedirectView("/sites/" + slug + "/edit", true);
        } else {
            if (published == null) {
                published = false;
            }
            Site s = Site.fromSlug(slug);

            AdminSites.canEdit(s);
            CMSTheme themeObj = CMSTheme.forType(theme);
            if (themeObj == null) {
                throw new ResourceNotFoundException();
            }
            editSite(name, description, themeObj, newSlug, published, s, viewGroup, postGroup, adminGroup, folder, analyticsCode,
                    accountId);
            return new RedirectView("/cms/sites/" + newSlug, true);
        }
    }

    @Atomic(mode = TxMode.WRITE)
    private void editSite(LocalizedString name, LocalizedString description, CMSTheme themeObj, String slug, Boolean published,
            Site s, String viewGroup, String postGroup, String adminGroup, String folder, String analyticsCode, String accountId) {

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
        if (!Strings.isNullOrEmpty(accountId)) {
            s.getGoogleAPIConnection().setAccountId(accountId);
        }

        s.setPublished(published);
        s.setCanViewGroup(Group.parse(viewGroup));
        s.setCanPostGroup(Group.parse(postGroup));
        s.setCanAdminGroup(Group.parse(adminGroup));
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

        GoogleAPIService service = Bennu.getInstance().getGoogleAPISerivce();
        // We provide a key
        if (!(clientId == null || clientId.equals("") || clientSecret == null || clientSecret.equals(""))) {
            // and we don't have one
            if (Bennu.getInstance().getGoogleAPISerivce() == null) {

                // lets set up the service
                makeNewGoogleAPIService(clientId, clientSecret);
            } else {
                // we already have, lets see if its the key is different or a
                // new private Key file.
                if (!service.getClientId().equals(clientId) || !service.getClientSecret().equals(clientSecret)) {
                    service.setClientId(clientId);
                    service.setClientSecret(clientSecret);
                }
            }
        } else {
            if (service != null) {
                service.delete();
            }
        }
    }

    private void makeNewGoogleAPIService(String clientId, String clientSecret) throws IOException {
        GoogleAPIService service;
        service = new GoogleAPIService();
        service.setClientId(clientId);
        service.setClientSecret(clientSecret);
        Bennu.getInstance().setGoogleAPISerivce(service);

    }

}