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
package org.fenixedu.cms.domain;

import java.io.StringWriter;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.cms.ui.SiteActivityRender;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SiteActivity extends SiteActivity_Base {

    public SiteActivity(Site site) {
        super();
        setSite(site);
    }

    public JsonElement getContent() {
        return new JsonParser().parse(super.getContent().toString());
    }

    protected static void makeActivity(Site site, JsonElement element) {
        SiteActivity activity = new SiteActivity(site);
        activity.setEventDate(new DateTime());
        activity.setContent(element);
        site.pushActivity(activity);
    }

    private static JsonObject siteActivity(Site site, User user, String type) {
        JsonObject object = new JsonObject();

        object.addProperty("type", type);
        object.addProperty("site", site.getExternalId());
        object.add("siteName", site.getName().json());
        object.addProperty("siteSlug", site.getSlug());
        object.addProperty("user", user.getUsername());
        object.addProperty("userName", user.getProfile().getDisplayName());

        makeActivity(site, object);

        return object;
    }

    public static void createdSite(Site site, User user) {
        siteActivity(site, user, "siteCreated");
    }

    public static void clonedSite(Site site, User user) {
        siteActivity(site, user, "siteCloned");
    }

    public static void importedSite(Site site, User user) {
        siteActivity(site, user, "siteImported");
    }

    public static void createdPost(Post post, User user) {
        JsonObject object = new JsonObject();

        object.addProperty("type", "postCreated");
        object.addProperty("post", post.getExternalId());
        object.add("postName", post.getName().json());
        object.addProperty("postSlug", post.getSlug());
        object.addProperty("user", user.getUsername());
        object.addProperty("userName", user.getProfile().getDisplayName());

        makeActivity(post.getSite(), object);
    }

    public static void editedPost(Post post, User user) {
        JsonObject object = new JsonObject();

        object.addProperty("type","postEdited");
        object.addProperty("post",post.getExternalId());
        object.add("postName", post.getName().json());
        object.addProperty("postSlug", post.getSlug());
        object.addProperty("user", user.getUsername());
        object.addProperty("userName", user.getProfile().getDisplayName());

        makeActivity(post.getSite(),object);
    }

    public static void deletedPost(Post post, Site site, User user){
        JsonObject object = new JsonObject();

        object.addProperty("type","postDeleted");
        object.addProperty("post", (String) null);
        object.add("postName", post.getName().json());
        object.addProperty("pageSlug",(String) null);
        object.addProperty("user", user.getUsername());
        object.addProperty("userName", user.getProfile().getDisplayName());

        makeActivity(site,object);
    }

    public static void recoveredPost(Post post, Site site, User user){
        JsonObject object = new JsonObject();

        object.addProperty("type","postRecovered");
        object.addProperty("post", post.getExternalId());
        object.add("postName", post.getName().json());
        object.addProperty("pageSlug",(String) null);
        object.addProperty("user", user.getUsername());
        object.addProperty("userName", user.getProfile().getDisplayName());

        makeActivity(site,object);
    }

    public static void createdPage(Page page, User user) {
        JsonObject object = new JsonObject();

        object.addProperty("type", "pageCreated");
        object.addProperty("post", page.getExternalId());
        object.add("pageName", page.getName().json());
        object.addProperty("pageSlug", page.getSlug());
        object.addProperty("user", user.getUsername());
        object.addProperty("userName", user.getProfile().getDisplayName());

        makeActivity(page.getSite(), object);
    }


    public static void editedPage(Page page, User user) {
        JsonObject object = new JsonObject();

        object.addProperty("type","pageEdited");
        object.addProperty("post",page.getExternalId());
        object.add("pageName", page.getName().json());
        object.addProperty("pageSlug", page.getSlug());
        object.addProperty("user", user.getUsername());
        object.addProperty("userName", user.getProfile().getDisplayName());

        makeActivity(page.getSite(),object);
    }

    public static void deletedPage(Page page, Site site, User user){
        JsonObject object = new JsonObject();

        object.addProperty("type","pageDeleted");
        object.addProperty("post", (String) null);
        object.add("pageName", page.getName().json());
        object.addProperty("pageSlug",(String) null);
        object.addProperty("user", user.getUsername());
        object.addProperty("userName", user.getProfile().getDisplayName());

        makeActivity(site,object);
    }

    public static void recoveredPage(Page page, Site site, User user) {
        JsonObject object = new JsonObject();

        object.addProperty("type","pageRecovered");
        object.addProperty("post", (String) null);
        object.add("pageName", page.getName().json());
        object.addProperty("pageSlug",(String) null);
        object.addProperty("user", user.getUsername());
        object.addProperty("userName", user.getProfile().getDisplayName());

        makeActivity(site,object);
    }

    public void delete() {
        setPrevious(null);
        setNext(null);
        setSite(null);
        setLastActivityLineSite(null);

        deleteDomainObject();
    }

    public String getRender() {

        StringWriter sw = new StringWriter();

        SiteActivityRender.render(this, sw);

        return sw.toString();
    }
}
