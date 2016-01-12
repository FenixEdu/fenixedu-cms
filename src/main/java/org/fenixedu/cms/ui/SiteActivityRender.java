package org.fenixedu.cms.ui;

import java.io.Writer;
import java.util.HashMap;
import java.util.function.BiConsumer;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.cms.domain.SiteActivity;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
        attachRenderer("siteImported", SiteActivityRender::siteImported);
        attachRenderer("siteCloned", SiteActivityRender::siteCloned);
        attachRenderer("pageCreated", SiteActivityRender::pageCreated);
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

    private static void pageCreated(SiteActivity activity, Writer writer) {
        JsonElement el = activity.getContent();
        JsonObject obj = el.getAsJsonObject();
        String postName = LocalizedString.fromJson(obj.get("pageName")).getContent();
        write(writer, obj.get("user").getAsString(), "created", postName);
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
