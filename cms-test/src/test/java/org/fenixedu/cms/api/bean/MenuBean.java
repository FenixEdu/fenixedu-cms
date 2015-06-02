package org.fenixedu.cms.api.bean;

import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.adapters.LocalizedStringViewer;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonObject;

public class MenuBean {
    private LocalizedString name;
    private String slug;
    private Boolean topMenu;

    public MenuBean() {
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

        if (getTopMenu() != null) {
            json.addProperty("topMenu", getTopMenu());
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

    public Boolean getTopMenu() {
        return topMenu;
    }

    public void setTopMenu(Boolean topMenu) {
        this.topMenu = topMenu;
    }

}