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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.cms.domain.SiteActivity;
import org.fenixedu.commons.i18n.LocalizedString;

import java.io.Writer;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class SiteActivityRender {

    private static HashMap<String, BiConsumer<SiteActivity, Writer>> map =
            new HashMap<String, BiConsumer<SiteActivity, Writer>>();

    public static void render(SiteActivity activity, Writer writer) {
        JsonElement el = activity.getContent();

        String type = el.getAsJsonObject().get("type").getAsString();
        if (type != null) {
            BiConsumer consumer = map.get(type);
            if (consumer != null) {
                consumer.accept(activity, writer);
            }
        }
    }

    public static void attachRenderer(String type, BiConsumer<SiteActivity, Writer> consumer) {
        map.put(type, consumer);
    }

    public static void init() {
        attachRenderer("siteCreated", SiteActivityRender::siteCreated);
        attachRenderer("postCreated", SiteActivityRender::postCreated);
        attachRenderer("postEdited",SiteActivityRender::postEdited);
        attachRenderer("postDeleted",SiteActivityRender::postDeleted);
        attachRenderer("postRecovered",SiteActivityRender::postRecovered);
        attachRenderer("siteImported", SiteActivityRender::siteImported);
        attachRenderer("siteCloned", SiteActivityRender::siteCloned);
        attachRenderer("pageCreated", SiteActivityRender::pageCreated);
        attachRenderer("pageEdited",SiteActivityRender::pageEdited);
        attachRenderer("pageDeleted",SiteActivityRender::pageDeleted);
        attachRenderer("pageRecovered",SiteActivityRender::pageRecovered);
    }

    private static void siteImported(SiteActivity activity, Writer writer) {
        JsonElement el = activity.getContent();
        JsonObject obj = el.getAsJsonObject();
        String postName = LocalizedString.fromJson(obj.get("siteName")).getContent();
        write(writer, obj.get("user").getAsString(), "imported", postName);
    }

    private static void siteCloned(SiteActivity activity, Writer writer) {
        JsonElement el = activity.getContent();
        JsonObject obj = el.getAsJsonObject();
        String postName = LocalizedString.fromJson(obj.get("siteName")).getContent();
        write(writer, obj.get("user").getAsString(), "cloned", postName);
    }

    private static void siteCreated(SiteActivity activity, Writer writer) {
        JsonElement el = activity.getContent();
        JsonObject obj = el.getAsJsonObject();
        String postName = LocalizedString.fromJson(obj.get("siteName")).getContent();
        write(writer, obj.get("user").getAsString(), "created", postName);
    }

    private static void postCreated(SiteActivity activity, Writer writer) {
        JsonElement el = activity.getContent();
        JsonObject obj = el.getAsJsonObject();
        String postName = LocalizedString.fromJson(obj.get("postName")).getContent();
        write(writer, obj.get("user").getAsString(), "created", postName);
    }

    private static void postEdited(SiteActivity activity, Writer writer) {
        JsonElement el = activity.getContent();
        JsonObject obj = el.getAsJsonObject();
        String postName = LocalizedString.fromJson(obj.get("postName")).getContent();
        write(writer, obj.get("user").getAsString(), "edited", postName);
    }

    private static void postDeleted(SiteActivity activity, Writer writer) {
        JsonElement el = activity.getContent();
        JsonObject obj = el.getAsJsonObject();
        String postName = LocalizedString.fromJson(obj.get("postName")).getContent();
        write(writer, obj.get("user").getAsString(), "deleted", postName);
    }

    private static void postRecovered(SiteActivity activity, Writer writer) {
        JsonElement el = activity.getContent();
        JsonObject obj = el.getAsJsonObject();
        String postName = LocalizedString.fromJson(obj.get("postName")).getContent();
        write(writer, obj.get("user").getAsString(), "recovered", postName);
    }

    private static void pageCreated(SiteActivity activity, Writer writer) {
        JsonElement el = activity.getContent();
        JsonObject obj = el.getAsJsonObject();
        String postName = LocalizedString.fromJson(obj.get("pageName")).getContent();
        write(writer, obj.get("user").getAsString(), "created", postName);
    }


    private static void pageEdited(SiteActivity activity, Writer writer) {
        JsonElement el = activity.getContent();
        JsonObject obj = el.getAsJsonObject();
        String postName = LocalizedString.fromJson(obj.get("pageName")).getContent();
        write(writer, obj.get("user").getAsString(), "edited", postName);
    }

    private static void pageDeleted(SiteActivity activity, Writer writer) {
        JsonElement el = activity.getContent();
        JsonObject obj = el.getAsJsonObject();
        String postName = LocalizedString.fromJson(obj.get("pageName")).getContent();
        write(writer, obj.get("user").getAsString(), "deleted", postName);
    }

    private static void pageRecovered(SiteActivity activity, Writer writer) {
        JsonElement el = activity.getContent();
        JsonObject obj = el.getAsJsonObject();
        String pageName = LocalizedString.fromJson(obj.get("pageName")).getContent();
        write(writer, obj.get("user").getAsString(), "recovered", pageName);
    }


    private static void write(Writer writer, String username, String action, String content) {
        try {
            User user = User.findByUsername(username);
            writer.write("<a href='#' class='avatar'><img src='"+ user.getProfile().getAvatarUrl() + "?s=32" +"' alt='"+ user.getProfile().getDisplayName()+"' /></a>");
            writer.write(
                "<p>" + "<strong>" + user.getProfile().getDisplayName() + "</strong> " + action
                + " " + content + " </p>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static {
        // Nastiness
        init();
    }
}
