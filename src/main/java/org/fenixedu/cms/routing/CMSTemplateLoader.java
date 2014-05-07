package org.fenixedu.cms.routing;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.fenixedu.cms.domain.CMSTemplateFile;
import org.fenixedu.cms.domain.CMSTheme;
import org.fenixedu.cms.domain.Site;

import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;

final class CMSTemplateLoader extends ClasspathLoader {
    private CMSTheme theme;

    public CMSTemplateLoader(CMSTheme theme) {
        this.theme = theme;
    }

    public CMSTemplateLoader(Site site) {
        this.theme = site.getTheme();
    }

    @Override
    public Reader getReader(String templateName) throws LoaderException {
        try {
            CMSTemplateFile file = this.theme.fileForPath(templateName);
            return new InputStreamReader(file.getStream(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new LoaderException(e, "Stuff");
        }
    }
}