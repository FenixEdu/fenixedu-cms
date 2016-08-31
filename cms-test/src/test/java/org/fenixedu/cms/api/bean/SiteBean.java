package org.fenixedu.cms.api.bean;

import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.api.json.LocalizedStringViewer;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonObject;

public class SiteBean {
    private LocalizedString name;
    private LocalizedString description;
    private String theme;
    private Boolean embedded;
    private String template;
    private String slug;
    private String analyticsCode;
    private String alternativeSite;
    private Boolean published;

    public SiteBean(LocalizedString name, LocalizedString description) {
        super();
        this.name = name;
        this.description = description;
    }

    public String toJson() {
        JsonObject json = new JsonObject();

        if (getName() != null) {
            json.add("name", new JsonBuilder().view(getName(), LocalizedStringViewer.class));
        }

        if (getDescription() != null) {
            json.add("description", new JsonBuilder().view(getDescription(), LocalizedStringViewer.class));
        }

        if (getTheme() != null) {
            json.addProperty("theme", getTheme());
        }

        if (getEmbedded() != null) {
            json.addProperty("embedded", getEmbedded());
        }

        if (getSlug() != null) {
            json.addProperty("slug", getSlug());
        }

        if (getAnalyticsCode() != null) {
            json.addProperty("analyticsCode", getAnalyticsCode());
        }

        if (getAlternativeSite() != null) {
            json.addProperty("alternativeSite", getAlternativeSite());
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

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(LocalizedString description) {
        this.description = description;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Boolean getEmbedded() {
        return embedded;
    }

    public void setEmbedded(Boolean embedded) {
        this.embedded = embedded;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getAnalyticsCode() {
        return analyticsCode;
    }

    public void setAnalyticsCode(String analyticsCode) {
        this.analyticsCode = analyticsCode;
    }

    public String getAlternativeSite() {
        return alternativeSite;
    }

    public void setAlternativeSite(String alternativeSite) {
        this.alternativeSite = alternativeSite;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }
}