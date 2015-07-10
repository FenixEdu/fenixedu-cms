package org.fenixedu.cms.ui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
        attachRenderer("siteImported", SiteActivityRender::siteImported);
        attachRenderer("siteCloned", SiteActivityRender::siteCloned);
    }

    private static void siteImported(SiteActivity activity, Writer writer) {
        JsonElement el = activity.getContent();

        JsonObject obj = el.getAsJsonObject();

        try {
            writer.write("<i class='glyphicon glyphicon-globe'></i> ");
            writer.write(obj.get("userName").getAsString() + " imported '"
                    + LocalizedString.fromJson(obj.get("siteName")).getContent() + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void siteCloned(SiteActivity activity, Writer writer) {
        JsonElement el = activity.getContent();

        JsonObject obj = el.getAsJsonObject();

        try {
            writer.write("<i class='glyphicon glyphicon-globe'></i> ");
            writer.write(obj.get("userName").getAsString() + " cloned '"
                    + LocalizedString.fromJson(obj.get("siteName")).getContent() + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void siteCreated(SiteActivity activity, Writer writer) {
        JsonElement el = activity.getContent();

        JsonObject obj = el.getAsJsonObject();

        try {
            writer.write("<i class='glyphicon glyphicon-globe'></i> ");
            writer.write(obj.get("userName").getAsString() + " created '"
                    + LocalizedString.fromJson(obj.get("siteName")).getContent() + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void postCreated(SiteActivity activity, Writer writer) {
        JsonElement el = activity.getContent();

        JsonObject obj = el.getAsJsonObject();

        try {
            writer.write("<i class='glyphicon glyphicon-pushpin'></i> ");
            writer.write(obj.get("userName").getAsString() + " created '"
                    + LocalizedString.fromJson(obj.get("postName")).getContent() + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static {
        // Nastiness
        init();
    }
}
