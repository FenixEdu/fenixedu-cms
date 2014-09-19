package org.fenixedu.cms.domain;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.cms.CMSConfigurationManager;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CMSTheme extends CMSTheme_Base {

    /**
     * The logged {@link User} creates a new instance of a {@link CMSTheme}
     */
    public CMSTheme() {
        this.setCreatedBy(Authenticate.getUser());
        this.setCreationDate(new DateTime());
    }

    /**
     * Searches for a {@link CMSTheme} with a given type.
     * 
     * @param t
     *            the type of the wanted {@link CMSTheme}.
     * @return
     *         the {@link CMSTheme} with the given type if it exists, or null otherwise.
     */
    public static CMSTheme forType(String t) {
        for (CMSTheme theme : Bennu.getInstance().getCMSThemesSet()) {
            if (theme.getType().equals(t)) {
                return theme;
            }
        }
        return null;
    }

    /**
     * Searches for a {@link CMSTemplate} with a given type on this theme.
     * 
     * @param t
     *            the type of the wanted {@link CMSTemplate}.
     * @return
     *         the {@link CMSTemplate} with the given type if it exists, or null otherwise.
     */
    public CMSTemplate templateForType(String t) {
        for (CMSTemplate template : this.getTemplatesSet()) {
            if (template.getType().equals(t)) {
                return template;
            }
        }
        return null;
    }

    /**
     * Searches for a {@link CMSThemeFile} with a given name on this theme.
     * 
     * @param path
     *            the displayName of the wanted {@link CMSThemeFile}.
     * @return
     *         the {@link CMSThemeFile} with the given displayName if it exists, or null otherwise.
     */
    public CMSThemeFile fileForPath(String path) {
        return getFiles().getFileForPath(path);
    }

    private static String getTypeForThemeFolder(String path) {
        try {
            FileInputStream fin = new FileInputStream(path + "/theme.json");
            JsonObject el = new JsonParser().parse(new BufferedReader(new InputStreamReader(fin))).getAsJsonObject();
            return el.get("type").getAsString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] contentForPath(String t) {
        String themeDevelopmentDirectory = CMSConfigurationManager.getConfiguration().themeDevelopmentDirectory();
        if (CoreConfiguration.getConfiguration().developmentMode() && themeDevelopmentDirectory != null
                && this.getType().equals(getTypeForThemeFolder(themeDevelopmentDirectory))) {
            try {
                return ByteStreams.toByteArray(new FileInputStream(themeDevelopmentDirectory + t));
            } catch (IOException e) {
                return null;
            }
        } else {
            CMSThemeFile file = this.fileForPath(t);
            return file == null ? null : file.getContent();
        }
    }

    public boolean definesPath(String string) {
        // FIXME Find a better way to do this!
        return contentForPath(string) != null;
    }

    /**
     * 
     * @return true if this is the default theme for the CMS, and false otherwise.
     */
    public boolean isDefault() {
        return Bennu.getInstance().getDefaultCMSTheme() == this;
    }

    @Atomic(mode = TxMode.WRITE)
    public void delete() {
        if (this.getChildrenSet().size() != 0) {
            throw new RuntimeException("Themes depend of this theme. Can't delete");
        }

        for (Site site : getSitesSet()) {
            site.setTheme(null);
        }
        this.setPrimaryBennu(null);
        this.setBennu(null);

        if (Bennu.getInstance().getCMSThemesSet().size() == 0) {
            Bennu.getInstance().setDefaultCMSTheme(null);
        } else {
            Bennu.getInstance().setDefaultCMSTheme(Bennu.getInstance().getCMSThemesSet().iterator().next());
        }

        this.setCreatedBy(null);
        this.setExtended(null);

        for (CMSTemplate template : this.getTemplatesSet()) {
            template.delete();
        }

        this.deleteDomainObject();

    }

    @Atomic
    public void changeFiles(CMSThemeFiles files) {
        if (getFiles() == null || !getFiles().checksumMatches(files)) {
            setFiles(files);
        }
    }

}
