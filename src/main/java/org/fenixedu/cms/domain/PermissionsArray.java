package org.fenixedu.cms.domain;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;

import java.io.Serializable;
import java.util.*;

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

        CREATE_POST(1),
        DELETE_POSTS(1),
        DELETE_POSTS_PUBLISHED(1),
        EDIT_POSTS(1),
        EDIT_POSTS_PUBLISHED(1),
        PUBLISH_POSTS(1),
        EDIT_PRIVATE_POSTS(1),
        SEE_PRIVATE_POSTS(1),
        DELETE_PRIVATE_POSTS(1),
        SEE_METADATA(1),
        EDIT_METADATA(1),
        DELETE_OTHERS_POSTS(1),
        EDIT_OTHERS_POSTS(1),
        CHANGE_OWNERSHIP_POST(1),
        SEE_PAGES(1),
        CREATE_PAGE(1),
        EDIT_PAGE(1),
        DELETE_PAGE(1),
        PUBLISH_PAGES(1),
        CHANGE_PATH_PAGES(1),
        CHANGE_VISIBILITY_PAGES(1),
        SEE_PAGE_COMPONENTS(1),
        EDIT_PAGE_COMPONENTS(1),
        DELETE_PAGE_COMPONENTS(1),
        LIST_CATEGORIES(1),
        CREATE_CATEGORY(1),
        EDIT_CATEGORY(1),
        DELETE_CATEGORY(1),
        LIST_MENUS(1),
        CREATE_MENU(1),
        EDIT_MENU(1),
        DELETE_MENU(1),
        CREATE_MENU_ITEM(1),
        EDIT_MENU_ITEM(1),
        DELETE_MENU_ITEM(1),
        CHOOSE_DEFAULT_PAGE(1),
        CHOOSE_PATH_AND_FOLDER(1),
        EDIT_SITE_INFORMATION(1),
        CHANGE_THEME(1),
        PUBLISH_SITE(1),
        MANAGE_ROLES(1),
        MANAGE_ANALYTICS(1);
        int order = 0;

        Permission(int order){
            this.order = order;
        }

        public LocalizedString getLocalizedName() {
            return BundleUtil.getLocalizedString("CmsPermissionResources", "label.permission.name." + name());
        }

        public LocalizedString getLocalizedDescription() {
            return BundleUtil.getLocalizedString("CmsPermissionResources", "label.permission.description." + name());
        }
    }
}


