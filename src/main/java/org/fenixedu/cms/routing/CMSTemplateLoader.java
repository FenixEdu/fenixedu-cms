package org.fenixedu.cms.routing;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    static String readFile(String path, Charset encoding) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return encoding.decode(ByteBuffer.wrap(encoded)).toString();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public Reader getReader(String templateName) throws LoaderException {
        try {
            if (theme.getType().equals("cms-default-theme")) {
                String text =
                        readFile("/home/borgez/workspace/cms/src/main/webapp/cms-default-theme/" + templateName,
                                StandardCharsets.UTF_8);
                return new InputStreamReader(new ByteArrayInputStream(text.getBytes()), "UTF-8");
            } else {
                CMSTemplateFile file = this.theme.fileForPath(templateName);
                return new InputStreamReader(file.getStream(), "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            throw new LoaderException(e, "Stuff");
        }
    }
}