package org.fenixedu.cms.api.bean;

import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.api.json.LocalizedStringViewer;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.cglib.core.Local;

public class PostBean {
    private LocalizedString name;
    private String slug;
    private LocalizedString body;
    private LocalizedString excerpt;
    private Boolean published;
    private String publicationBegin;
    private String publicationEnd;

    public PostBean() {
        super();
    }

    public String toJson() {
        JsonObject json = new JsonObject();

        json.add("name", new JsonBuilder().view(getName(), LocalizedStringViewer.class));
        json.add("body", new JsonBuilder().view(getBody(), LocalizedStringViewer.class));
        json.add("excerpt", new JsonBuilder().view(getExcerpt(), LocalizedStringViewer.class));

        if (getSlug() != null) {
            json.addProperty("slug", getSlug());
        }

        if (getPublished() != null) {
            json.addProperty("published", getPublished());
        }

        if (getPublicationBegin() != null) {
            json.addProperty("publicationBegin", getPublicationBegin());
        }

        if (getPublicationEnd() != null) {
            json.addProperty("publicationEnd", getPublicationEnd());
        }

        return json.toString();
    }

    public LocalizedString getName() {
        return name;
    }

    public void setName(LocalizedString name) {
        this.name = name;
    }

    public LocalizedString getBody() {
        return body;
    }

    public void setBody(LocalizedString body) {
        this.body = body;
    }

    public void setExcerpt(LocalizedString excerpt) {
        this.excerpt = excerpt;
    }

    public LocalizedString getExcerpt() {
        return excerpt;
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

    public String getPublicationBegin() {
        return publicationBegin;
    }

    public void setPublicationBegin(String publicationBegin) {
        this.publicationBegin = publicationBegin;
    }

    public String getPublicationEnd() {
        return publicationEnd;
    }

    public void setPublicationEnd(String publicationEnd) {
        this.publicationEnd = publicationEnd;
    }

}