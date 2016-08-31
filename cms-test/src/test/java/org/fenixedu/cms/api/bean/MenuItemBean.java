package org.fenixedu.cms.api.bean;

import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.api.json.LocalizedStringViewer;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonObject;

public class MenuItemBean {
    private LocalizedString name;
    private String url;
    private Boolean folder;
    private Integer position;

    public String toJson() {
        JsonObject json = new JsonObject();

        if (getName() != null) {
            json.add("name", new JsonBuilder().view(getName(), LocalizedStringViewer.class));
        }

        if (getFolder() != null) {
            json.addProperty("folder", getFolder());
        }

        if (getUrl() != null) {
            json.addProperty("url", getUrl());
        }

        if (getPosition() != null) {
            json.addProperty("position", getPosition());
        }
        return json.toString();
    }

    public LocalizedString getName() {
        return name;
    }

    public void setName(LocalizedString name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getFolder() {
        return folder;
    }

    public void setFolder(Boolean folder) {
        this.folder = folder;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

}