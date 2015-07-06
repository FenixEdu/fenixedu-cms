package org.fenixedu.cms.api.bean;

import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.api.json.LocalizedStringViewer;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonObject;

public class PageBean {
    private LocalizedString name;
    private String slug;
    private Boolean published;

    public PageBean() {
        super();
    }

    public String toJson() {
        JsonObject json = new JsonObject();

        if (getName() != null) {
            json.add("name", new JsonBuilder().view(getName(), LocalizedStringViewer.class));
        }

        if (getSlug() != null) {
            json.addProperty("slug", getSlug());
        }

        if (getPublished() != null) {
            json.addProperty("published", getPublished());
        }

        return json.toString();
    }

    public LocalizedString getName() {
        return name;
    }

    public void setName(LocalizedString name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }
}