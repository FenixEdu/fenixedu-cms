package org.fenixedu.cms.domain;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.fenixedu.commons.i18n.LocalizedString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

/**
 * Created by borgez-dsi on 03-09-2015.
 */
public class PermissionsArray implements Serializable {

    private static final JsonParser JSON_PARSER = new JsonParser();
    private final EnumSet<Permission> permissions;

    public PermissionsArray(EnumSet<Permission> permissions) {
        this.permissions = permissions;
    }

    public static PermissionsArray fromJson(JsonElement json) {
        List<Permission> permissionList = new ArrayList<>();
        for(JsonElement permissionJson : json.getAsJsonArray()) {
            permissionList.add(Permission.valueOf(permissionJson.getAsJsonPrimitive().getAsString()));
        }
        return new PermissionsArray(EnumSet.copyOf(permissionList));
    }

    public String externalize() {
        return json().toString();
    }

    public JsonArray json() {
        JsonArray json = new JsonArray();
        permissions.stream().map(Permission::name).map(JsonPrimitive::new).forEach(json::add);
        return json;
    }

    public static PermissionsArray internalize(String json) {
        return fromJson(JSON_PARSER.parse(json));
    }

    public static EnumSet<Permission> all() {
        return EnumSet.allOf(Permission.class);
    }

    public EnumSet<Permission> get() {
        return permissions;
    }

    public enum Permission {

        DELETE_POSTS,
        DELETE_POSTS_PUBLISHED,
        EDIT_POSTS,
        EDIT_POSTS_PUBLISHED,
        PUBLISH_POSTS,
        EDIT_PRIVATE_POSTS,
        SEE_PRIVATE_POSTS,
        DELETE_PRIVATE_POSTS,
        SEE_METADATA,
        EDIT_METADATA,
        DELETE_OTHERS_POSTS,
        EDIT_OTHERS_POSTS,
        CHANGE_OWNERSHIP_POST,
        SEE_PAGES,
        CREATE_PAGE,
        EDIT_PAGE,
        DELETE_PAGE,
        PUBLISH_PAGES,
        CHANGE_PATH_PAGES,
        CHANGE_VISIBILITY_PAGES,
        SEE_PAGE_COMPONENTS,
        EDIT_PAGE_COMPONENTS,
        DELETE_PAGE_COMPONENTS,
        LIST_CATEGORIES,
        CREATE_CATEGORY,
        EDIT_CATEGORY,
        DELETE_CATEGORY,
        LIST_MENUS,
        CREATE_MENU,
        EDIT_MENU,
        DELETE_MENU,
        CREATE_MENU_ITEM,
        EDIT_MENU_ITEM,
        DELETE_MENU_ITEM,
        CHOOSE_DEFAULT_PAGE,
        CHOOSE_PATH_AND_FOLDER,
        EDIT_SITE_INFORMATION,
        CHANGE_THEME,
        PUBLISH_SITE,
        HIDE_SITE,
        MANAGE_ROLES,
        ATTACH_USERS_TO_ROLES,
        MANAGE_ANALYTICS;

        public LocalizedString getLocalizedName() {
            return new LocalizedString(Locale.getDefault(), name());
        }
    }
}


