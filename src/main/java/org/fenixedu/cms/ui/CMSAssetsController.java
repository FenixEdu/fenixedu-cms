package org.fenixedu.cms.ui;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.cms.domain.CMSTheme;
import org.fenixedu.cms.domain.CMSThemeFile;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;

@Controller
@RequestMapping("/cms/assets")
public class CMSAssetsController {

    private static final String expires = DateTime.now().plusYears(1).toString("E, d MMM yyyy HH:mm:ss z");

    @RequestMapping("/{type}/{hash}/**")
    public void asset(@PathVariable String type, @PathVariable String hash, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        CMSTheme theme = CMSTheme.forType(type);
        if (theme == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, type);
            return;
        }
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        path = path.substring(("/cms/assets/" + theme.getType() + "/" + hash + "/").length());
        CMSThemeFile file = theme.fileForPath("static/" + path);
        if (file == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, path);
            return;
        }
        byte[] bytes = file.getContent();
        String etag = "W/\"" + bytes.length + "-" + file.getLastModified().getMillis() + "\"";

        response.setHeader("ETag", etag);
        response.setHeader("Expires", expires);
        response.setHeader("Cache-Control", "max-age=31536000");

        if (etag.equals(request.getHeader("If-None-Match"))) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        } else {
            response.setContentLength(bytes.length);
            response.setContentType(file.getContentType());
            try (OutputStream stream = response.getOutputStream()) {
                stream.write(bytes);
            }
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
