package org.fenixedu.cms.domain;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.groups.PersistentDynamicGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.exceptions.CmsDomainException;

public class CmsSettings extends CmsSettings_Base {

    public CmsSettings() {
        PersistentDynamicGroup managers = (PersistentDynamicGroup) Group.parse("#managers").toPersistentGroup();
        setFoldersManagers(managers);
        setRolesManagers(managers);
        setSettingsManagers(managers);
        setThemesManagers(managers);
    }

    public boolean canManageFolders() {
        return getFoldersManagers().isMember(Authenticate.getUser());
    }

    public boolean canManageRoles() {
        return getRolesManagers().isMember(Authenticate.getUser());
    }

    public boolean canManageSettings() {
        return getSettingsManagers().isMember(Authenticate.getUser());
    }

    public boolean canManageThemes() {
        return getThemesManagers().isMember(Authenticate.getUser());
    }

    public boolean canManageGloabalPermissions() {
        return Group.parse("#managers").isMember(Authenticate.getUser());
    }

    public void ensureCanManageFolders() {
        if (!canManageFolders()) {
            throw CmsDomainException.forbiden();
        }
    }

    public void ensureCanManageRoles() {
        if (!canManageRoles()) {
            throw CmsDomainException.forbiden();
        }
    }

    public void ensureCanManageSettings() {
        if (!canManageSettings()) {
            throw CmsDomainException.forbiden();
        }
    }

    public void ensureCanManageThemes() {
        if (!canManageThemes()) {
            throw CmsDomainException.forbiden();
        }
    }

    public void ensureCanManageGlobalPermissions() {
        if (!canManageGloabalPermissions()) {
            throw CmsDomainException.forbiden();
        }
    }

    public static CmsSettings getInstance() {
        return Bennu.getInstance().getCmsSettings();
    }

}
