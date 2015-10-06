package org.fenixedu.cms.domain;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.cms.domain.PermissionsArray.Permission;
import org.fenixedu.commons.i18n.LocalizedString;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.fenixedu.cms.domain.PermissionsArray.Permission.EDIT_MENU_ITEM;

public class DefaultRoles {

  private static final String ADMIN_ROLE_NAME = "roles.admin";
  private static final String EDITOR_ROLE_NAME = "roles.editor";
  private static final String AUTHOR_ROLE_NAME = "roles.author";
  private static final String CONTRIBUTOR_ROLE_NAME = "roles.contributor";
  private static final String DEFAULT_BUNDLE = "CmsPermissionResources";
  private static DefaultRoles instance;
  private RoleTemplate adminRole;
  private RoleTemplate editorRole;
  private RoleTemplate contributorRole;
  private RoleTemplate authorRole;

  private DefaultRoles() {
    init();
  }

  public void init() {
    this.adminRole = getOrCreateTemplateWithPermissions(getAdminPermissions(), ADMIN_ROLE_NAME);
    this.editorRole = getOrCreateTemplateWithPermissions(getEditorPermissions(), EDITOR_ROLE_NAME);
    this.authorRole = getOrCreateTemplateWithPermissions(getAuthorPermissions(), AUTHOR_ROLE_NAME);
    this.contributorRole = getOrCreateTemplateWithPermissions(getContributorPermissions(), CONTRIBUTOR_ROLE_NAME);
  }

  public RoleTemplate getOrCreateTemplateWithPermissions(Set<Permission> permissions, String description) {
    return getTemplateWithPermissions(permissions)
        .orElseGet(() -> createTemplate(permissions, description));
  }

  public Optional<RoleTemplate> getTemplateWithPermissions(Set<Permission> permissions) {
    Collection<RoleTemplate> templates = Bennu.getInstance().getRoleTemplatesSet();
    return templates.stream().filter(template->template.getPermissions().get().equals(permissions)).findAny();
  }

  private RoleTemplate createTemplate(Set<Permission> permissions, String description) {
    RoleTemplate template = new RoleTemplate();
    template.setPermissions(new PermissionsArray(EnumSet.copyOf(permissions)));
    template.setDescription(BundleUtil.getLocalizedString(DEFAULT_BUNDLE, description));
    return template;
  }

  private Set<Permission> getAdminPermissions() {
    Set<Permission> permissions = new HashSet<>();
    permissions.add(Permission.CREATE_POST);
    permissions.add(Permission.CREATE_PAGE);
    permissions.add(Permission.SEE_PAGES);
    permissions.add(Permission.SEE_PAGE_COMPONENTS);
    permissions.add(Permission.DELETE_OTHERS_POSTS);
    permissions.add(Permission.DELETE_PAGE);
    permissions.add(Permission.DELETE_POSTS);
    permissions.add(Permission.DELETE_PRIVATE_POSTS);
    permissions.add(Permission.DELETE_POSTS_PUBLISHED);
    permissions.add(Permission.EDIT_OTHERS_POSTS);
    permissions.add(Permission.EDIT_PAGE);
    permissions.add(Permission.EDIT_POSTS);
    permissions.add(Permission.EDIT_POSTS_PUBLISHED);
    permissions.add(Permission.LIST_CATEGORIES);
    permissions.add(Permission.EDIT_CATEGORY);
    permissions.add(Permission.DELETE_CATEGORY);
    permissions.add(Permission.CREATE_CATEGORY);
    permissions.add(Permission.MANAGE_ANALYTICS);
    permissions.add(Permission.MANAGE_ROLES);
    permissions.add(Permission.PUBLISH_PAGES);
    permissions.add(Permission.PUBLISH_POSTS);
    permissions.add(Permission.PUBLISH_SITE);
    permissions.add(Permission.CREATE_MENU);
    permissions.add(Permission.DELETE_MENU);
    permissions.add(Permission.LIST_MENUS);
    permissions.add(Permission.EDIT_MENU);
    permissions.add(Permission.CREATE_MENU_ITEM);
    permissions.add(Permission.DELETE_MENU_ITEM);
    permissions.add(Permission.EDIT_MENU_ITEM);
    permissions.add(Permission.CHANGE_PATH_PAGES);
    permissions.add(Permission.CHOOSE_PATH_AND_FOLDER);
    return permissions;
  }

  private Set<Permission> getEditorPermissions() {
      Set<Permission> permissions = new HashSet<>();
      permissions.add(Permission.CREATE_POST);
      permissions.add(Permission.CREATE_PAGE);
      permissions.add(Permission.SEE_PAGES);
      permissions.add(Permission.DELETE_OTHERS_POSTS);
      permissions.add(Permission.DELETE_PAGE);
      permissions.add(Permission.DELETE_POSTS);
      permissions.add(Permission.DELETE_PRIVATE_POSTS);
      permissions.add(Permission.DELETE_POSTS_PUBLISHED);
      permissions.add(Permission.EDIT_OTHERS_POSTS);
      permissions.add(Permission.EDIT_PAGE);
      permissions.add(Permission.EDIT_POSTS);
      permissions.add(Permission.EDIT_POSTS_PUBLISHED);
      permissions.add(Permission.LIST_CATEGORIES);
      permissions.add(Permission.EDIT_CATEGORY);
      permissions.add(Permission.DELETE_CATEGORY);
      permissions.add(Permission.CREATE_CATEGORY);
      permissions.add(Permission.PUBLISH_PAGES);
      permissions.add(Permission.PUBLISH_POSTS);

      permissions.add(Permission.LIST_MENUS);
      permissions.add(Permission.EDIT_MENU);
      permissions.add(Permission.CREATE_MENU_ITEM);
      permissions.add(Permission.DELETE_MENU_ITEM);
      permissions.add(Permission.EDIT_MENU_ITEM);

    return permissions;
  }

  private Set<Permission> getAuthorPermissions() {
    Set<Permission> permissions = new HashSet<>();
    permissions.add(Permission.CREATE_POST);
    permissions.add(Permission.DELETE_POSTS);
    permissions.add(Permission.DELETE_POSTS_PUBLISHED);
    permissions.add(Permission.EDIT_POSTS);
    permissions.add(Permission.EDIT_POSTS_PUBLISHED);
    permissions.add(Permission.LIST_CATEGORIES);
    permissions.add(Permission.CREATE_CATEGORY);
    permissions.add(Permission.PUBLISH_POSTS);
    permissions.add(Permission.LIST_MENUS);
    return permissions;
  }

  private Set<Permission> getContributorPermissions() {
    Set<Permission> permissions = new HashSet<>();
    permissions.add(Permission.CREATE_POST);
    permissions.add(Permission.DELETE_POSTS);
    permissions.add(Permission.EDIT_POSTS);
    permissions.add(Permission.LIST_CATEGORIES);
    return permissions;
  }

  public static DefaultRoles getInstance() {
    if(instance == null) {
      instance = new DefaultRoles();
    }
    return instance;
  }

  public RoleTemplate getAdminRole() {
    return adminRole;
  }

  public RoleTemplate getEditorRole() {
    return editorRole;
  }

  public RoleTemplate getContributorRole() {
    return contributorRole;
  }

  public RoleTemplate getAuthorRole() {
    return authorRole;
  }
}
