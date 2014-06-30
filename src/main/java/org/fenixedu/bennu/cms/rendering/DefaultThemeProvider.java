package org.fenixedu.bennu.cms.rendering;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

import org.fenixedu.bennu.cms.domain.CMSThemeLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ThemeProvider
public class DefaultThemeProvider implements ProvidesThemes {

    public Logger LOGGER = LoggerFactory.getLogger(DefaultThemeProvider.class);

    @Override
    public void loadThemes() {
        InputStream in = CMSThemeLoader.class.getResourceAsStream("/META-INF/resources/WEB-INF/cms-default-theme.zip");
        ZipInputStream zin = new ZipInputStream(in);
        try {
            CMSThemeLoader.createFromZipStream(zin);
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage());
        }
    }

}
