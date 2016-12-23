/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of FenixEdu CMS.
 *
 * FenixEdu CMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu CMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu CMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu;

import pt.ist.fenixframework.FenixFramework;

import java.util.EnumSet;

import org.fenixedu.bennu.core.bootstrap.AdminUserBootstrapper;
import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrap;
import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrapper;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.cms.domain.CmsSettings;
import org.fenixedu.cms.domain.PermissionsArray;
import org.fenixedu.cms.domain.PermissionsArray.Permission;
import org.fenixedu.cms.domain.RoleTemplate;

@Bootstrapper(bundle = "resources.CmsResources", name = "application.title.cms.bootstrapper", after = AdminUserBootstrapper.class, sections = {})
public class CmsBootstrapper {
    
    private static final String ADMIN_ROLE_NAME = "roles.admin";
    private static final String EDITOR_ROLE_NAME = "roles.editor";
    private static final String AUTHOR_ROLE_NAME = "roles.author";
    private static final String CONTRIBUTOR_ROLE_NAME = "roles.contributor";
    private static final String DEFAULT_BUNDLE = "CmsPermissionResources";

    @Bootstrap
    public static void bootstrapCms() {
        if (Bennu.getInstance().getCmsSettings() == null) {
            FenixFramework.atomic(() -> Bennu.getInstance().setCmsSettings(new CmsSettings()));
        }
        
        if (Bennu.getInstance().getRoleTemplatesSet().isEmpty()) {
            initDefaultRoles();
        }
    }
    
    public static void initDefaultRoles() {
        createTemplate(getAdminPermissions(), ADMIN_ROLE_NAME);
        createTemplate(getEditorPermissions(), EDITOR_ROLE_NAME);
        createTemplate(getAuthorPermissions(), AUTHOR_ROLE_NAME);
        createTemplate(getContributorPermissions(), CONTRIBUTOR_ROLE_NAME);
    }
    
    private static RoleTemplate createTemplate(EnumSet<Permission> permissions, String description) {
        RoleTemplate template = new RoleTemplate();
        template.setPermissions(new PermissionsArray(permissions));
        template.setName(BundleUtil.getLocalizedString(DEFAULT_BUNDLE, description));
        return template;
    }
    
    private static EnumSet<Permission> getEditorPermissions() {
        return EnumSet.of(Permission.CREATE_POST, Permission.CREATE_PAGE, Permission.SEE_PAGES, Permission.SEE_POSTS,
            Permission.SEE_PRIVATE_POSTS, Permission.DELETE_OTHERS_POSTS, Permission.DELETE_PAGE, Permission.DELETE_POSTS,
            Permission.DELETE_PRIVATE_POSTS, Permission.DELETE_POSTS_PUBLISHED, Permission.EDIT_OTHERS_POSTS,
            Permission.EDIT_PAGE, Permission.EDIT_POSTS, Permission.EDIT_POSTS_PUBLISHED, Permission.LIST_CATEGORIES,
            Permission.EDIT_CATEGORY, Permission.DELETE_CATEGORY, Permission.CREATE_CATEGORY, Permission.PUBLISH_PAGES,
            Permission.PUBLISH_POSTS, Permission.LIST_MENUS, Permission.EDIT_MENU, Permission.CREATE_MENU_ITEM,
            Permission.DELETE_MENU_ITEM, Permission.EDIT_MENU_ITEM);
    }
    
    private static EnumSet<Permission> getAuthorPermissions() {
        return EnumSet
            .of(Permission.CREATE_POST, Permission.DELETE_POSTS, Permission.DELETE_POSTS_PUBLISHED, Permission.SEE_POSTS,
                Permission.EDIT_POSTS, Permission.EDIT_POSTS_PUBLISHED, Permission.LIST_CATEGORIES, Permission.CREATE_CATEGORY,
                Permission.PUBLISH_POSTS, Permission.LIST_MENUS);
    }
    
    private static EnumSet<Permission> getContributorPermissions() {
        return EnumSet.of(Permission.CREATE_POST, Permission.SEE_POSTS, Permission.DELETE_POSTS, Permission.EDIT_POSTS,
            Permission.LIST_CATEGORIES);
    }
    
    private static EnumSet<Permission> getAdminPermissions() {
        return EnumSet.of(Permission.CREATE_POST, Permission.CREATE_PAGE, Permission.SEE_POSTS, Permission.SEE_PRIVATE_POSTS,
            Permission.SEE_PAGES, Permission.SEE_PAGE_COMPONENTS, Permission.DELETE_OTHERS_POSTS, Permission.DELETE_PAGE,
            Permission.DELETE_POSTS, Permission.DELETE_PRIVATE_POSTS, Permission.DELETE_POSTS_PUBLISHED,
            Permission.EDIT_OTHERS_POSTS, Permission.EDIT_PAGE, Permission.EDIT_POSTS, Permission.EDIT_POSTS_PUBLISHED,
            Permission.EDIT_SITE_INFORMATION, Permission.LIST_CATEGORIES, Permission.EDIT_CATEGORY, Permission.DELETE_CATEGORY,
            Permission.CREATE_CATEGORY, Permission.MANAGE_ANALYTICS, Permission.MANAGE_ROLES, Permission.PUBLISH_PAGES,
            Permission.PUBLISH_POSTS, Permission.PUBLISH_SITE, Permission.CREATE_MENU, Permission.DELETE_MENU,
            Permission.LIST_MENUS, Permission.EDIT_MENU, Permission.CREATE_MENU_ITEM, Permission.DELETE_MENU_ITEM,
            Permission.EDIT_MENU_ITEM, Permission.CHANGE_PATH_PAGES, Permission.CHOOSE_PATH_AND_FOLDER,
            Permission.EDIT_SITE_INFORMATION, Permission.CHOOSE_DEFAULT_PAGE);
    }
    
}
