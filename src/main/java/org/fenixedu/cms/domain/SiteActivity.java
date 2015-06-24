package org.fenixedu.cms.domain;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.cms.ui.SiteActivityRender;
import org.joda.time.DateTime;

import java.io.StringWriter;

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
