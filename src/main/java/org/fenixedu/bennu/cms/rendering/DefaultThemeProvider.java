package org.fenixedu.bennu.cms.rendering;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletContext;

import org.fenixedu.bennu.cms.domain.CMSThemeLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultThemeProvider implements ThemeProvider {

    private static final Logger logger = LoggerFactory.getLogger(DefaultThemeProvider.class);

    @Override
    public void registerThemes(ServletContext context) {
        try {
            InputStream in = context.getResourceAsStream("/WEB-INF/cms-default-theme.zip");
            ZipInputStream zin = new ZipInputStream(in);
            CMSThemeLoader.createFromZipStream(zin);
        } catch (RuntimeException e) {
            logger.error("Could not load default theme - sites may not work!", e);
        }
    }

}
