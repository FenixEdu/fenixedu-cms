package org.fenixedu.cms.domain;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.Profile;
import com.google.api.services.analytics.model.Profiles;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.social.domain.api.GoogleAPI;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class SiteAnalytics implements Serializable {
  private static final String LAST_UPDATE_PROPERTY = "$$last_update";
  private static final Logger LOGGER = LoggerFactory.getLogger(SiteAnalytics.class);
  private static final long serialVersionUID = 4890885803531605616L;
  private static final long MAX_UPDATE_DURATION = Duration.ofHours(24).toNanos();
  private final JsonElement metadata;

  public SiteAnalytics() {
    this(new JsonObject());
  }

  public SiteAnalytics(JsonElement metadata) {
    this.metadata = new Gson().fromJson(metadata.toString(), JsonElement.class);
  }

  public JsonElement externalize() {
    return metadata;
  }

  public static SiteAnalytics internalize(JsonElement json) {
    return new SiteAnalytics(json);
  }

  public JsonElement getOrFetch(Site site) {
    if(!isLastUpdateValid()) {
      return update(site).get();
    }
    return this.metadata;
  }

  public JsonElement get() {
    return this.metadata;
  }

  private SiteAnalytics update(Site site) {
    SiteAnalytics siteAnalytics = new SiteAnalytics(fetch(site));
    site.setAnalytics(siteAnalytics);
    return siteAnalytics;
  }

  private boolean isLastUpdateValid() {
    return Optional.ofNullable(this.metadata).filter(JsonElement::isJsonObject)
        .map(JsonElement::getAsJsonObject).map(jsonObj->jsonObj.get(LAST_UPDATE_PROPERTY))
        .filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsString).map(DateTime::parse)
        .filter(lastUpdate -> lastUpdate.minus(MAX_UPDATE_DURATION).isBeforeNow()).isPresent();

  }

  private JsonObject fetch(Site site) {
    JsonObject data = new JsonObject();
    data.addProperty(LAST_UPDATE_PROPERTY, DateTime.now().toDateTimeISO().toString());
    try {
        if (!Strings.isNullOrEmpty(site.getAnalyticsAccountId()) && !Strings.isNullOrEmpty(
            site.getAnalyticsCode())) {
          Analytics analytics = getUserAnalytics();
          Profiles profiles = analytics.management().profiles().list(
              site.getAnalyticsAccountId(), site.getAnalyticsCode()).execute();
          for (Profile profile : profiles.getItems()) {
            GaData query = analytics.data().ga()
                .get("ga:" + profile.getId(), "30daysAgo", "today",
                     "ga:pageviews,ga:visitors")
                .setDimensions("ga:date")
                .execute();

            for (List<String> days : query.getRows()) {
              JsonObject views;
              if (data.has(days.get(0))) {
                views = (JsonObject) data.get(days.get(0));
                views.addProperty("pageviews", views.get("pageviews") + days.get(1));
                views.addProperty("visitors", views.get("visitors") + days.get(2));
              } else {
                views = new JsonObject();
                data.add(days.get(0), views);
              }
              views.addProperty("pageviews", days.get(1));
              views.addProperty("visitors", days.get(2));
            }
          }

        }
      } catch(Exception e) {
        LOGGER.error("Error loading analytics data for site '" + site.getSlug() + "'", e);
      }

      return data;
  }

  private Analytics getUserAnalytics() {
    GoogleCredential
        credential = GoogleAPI.getInstance().getAuthenticatedUser(Authenticate.getUser()).get().getAuthenticatedSDK();
    return new Analytics.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential).setApplicationName(
        PortalConfiguration.getInstance().getApplicationTitle().getContent(Locale.ENGLISH)).build();
  }

}
