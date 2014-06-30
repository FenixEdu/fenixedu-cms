package org.fenixedu.bennu.cms.routing;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.fenixedu.bennu.cms.domain.CMSTheme;
import org.fenixedu.bennu.cms.domain.Site;

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
            return new InputStreamReader(this.theme.streamForPath(templateName), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new LoaderException(e, "Stuff");
        }
    }
}