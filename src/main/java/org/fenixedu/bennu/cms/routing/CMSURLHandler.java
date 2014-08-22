package org.fenixedu.bennu.cms.routing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.cms.CMSConfigurationManager;
import org.fenixedu.bennu.cms.domain.CMSTheme;
import org.fenixedu.bennu.cms.domain.CMSThemeFile;
import org.fenixedu.bennu.cms.domain.Component;
import org.fenixedu.bennu.cms.domain.Page;
import org.fenixedu.bennu.cms.domain.Site;
import org.fenixedu.bennu.cms.exceptions.ResourceNotFoundException;
import org.fenixedu.bennu.cms.rendering.CMSExtensions;
import org.fenixedu.bennu.cms.rendering.TemplateContext;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.portal.servlet.SemanticURLHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public final class CMSURLHandler implements SemanticURLHandler {

    private static final Logger logger = LoggerFactory.getLogger(CMSURLHandler.class);

    private final PebbleEngine engine = new PebbleEngine(new ClasspathLoader() {
        @Override
        public Reader getReader(String templateName) throws LoaderException {
            String[] parts = templateName.split("/", 2);

            if (parts.length != 2) {
                throw new IllegalArgumentException("Not a valid name: " + templateName);
            }
            CMSTheme theme = CMSTheme.forType(parts[0]);
            if (theme == null) {
                throw new IllegalArgumentException("Theme " + parts[0] + " not found!");
            }

            byte[] bytes = theme.contentForPath(parts[1]);
            if (bytes == null) {
                throw new IllegalArgumentException("Theme " + parts[0] + " does not contain resource '" + parts[1] + '"');
            }
            return new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8);
        }
    });

    public CMSURLHandler() {
        engine.addExtension(new CMSExtensions());
        if (CMSConfigurationManager.isInThemeDevelopmentMode()) {
            engine.setTemplateCache(null);
            logger.info("CMS Theme Development Mode enabled!");
        } else {
            engine.setTemplateCache(CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build());
        }
    }

    public static String rewritePageUrl(HttpServletRequest request) {
        String requestURL = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        if (requestURL.endsWith("/")) {
            requestURL = requestURL.substring(0, requestURL.length() - 1);
        }
        return queryString == null ? requestURL : requestURL + "?" + queryString;
    }

    @Override
    public void handleRequest(MenuFunctionality menu, final HttpServletRequest req, HttpServletResponse res, FilterChain fc)
            throws IOException, ServletException {
        Site site = menu.getSites();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        Writer bufWriter = new OutputStreamWriter(buf);

        if (site.getCanViewGroup().isMember(Authenticate.getUser())) {
            if (site.getPublished()) {
                try {
                    String pageSlug = req.getRequestURI().substring(req.getContextPath().length() + menu.getFullPath().length());
                    if (pageSlug.endsWith("/")) {
                        handleLeadingSlash(req, res, site, bufWriter);
                    } else if (pageSlug.startsWith("/static/")) {
                        handleStaticResource(req, res, site, buf, bufWriter, pageSlug);
                    } else {
                        renderCMSPage(req, res, site, bufWriter, pageSlug);
                    }
                } catch (Exception e) {
                    buf.reset();
                    logger.error("Exception while rendering CMS page " + req.getRequestURI(), e);
                    errorPage(req, res, bufWriter, site, 500);
                }
            } else {
                errorPage(req, res, bufWriter, site, 404);
            }
        } else {
            errorPage(req, res, bufWriter, site, 403);
        }
        res.getOutputStream().write(buf.toByteArray(), 0, buf.size());
    }

    private void handleStaticResource(final HttpServletRequest req, HttpServletResponse res, Site sites,
            ByteArrayOutputStream buf, Writer bufWriter, String pageSlug) throws IOException, ServletException {
        pageSlug = pageSlug.replaceFirst("/", "");
        byte[] bytes = sites.getTheme().contentForPath(pageSlug);
        if (bytes != null) {
            CMSThemeFile templateFile = sites.getTheme().fileForPath(pageSlug);
            buf.write(bytes);
            res.setContentLength(bytes.length);
            if (templateFile != null) {
                res.setContentType(templateFile.getContentType());
            } else {
                res.setContentType(new MimetypesFileTypeMap().getContentType(pageSlug));
            }
        } else {
            errorPage(req, res, bufWriter, sites, 404);
        }
    }

    private void renderCMSPage(final HttpServletRequest req, HttpServletResponse res, Site sites, Writer bufWriter,
            String pageSlug) throws ServletException, IOException, PebbleException {
        pageSlug = pageSlug.replace("/", "");

        Page page = sites.pageForSlug(pageSlug);

        if (page == null || page.getTemplate() == null) {
            errorPage(req, res, bufWriter, sites, 404);
        } else {
            try {
                renderPage(req, pageSlug, res, bufWriter, sites, page);
            } catch (ResourceNotFoundException e) {
                errorPage(req, res, bufWriter, sites, 404);
            }
        }
    }

    private void handleLeadingSlash(final HttpServletRequest req, HttpServletResponse res, Site sites, Writer bufWriter)
            throws PebbleException, IOException, ServletException {
        if (req.getMethod().equals("GET")) {
            res.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            res.setHeader("Location", rewritePageUrl(req));
            return;
        } else if (req.getMethod().equals("POST")) {
            if (CoreConfiguration.getConfiguration().developmentMode()) {
                PebbleEngine engine = new PebbleEngine(new StringLoader());
                engine.addExtension(new CMSExtensions());
                PebbleTemplate compiledTemplate =
                        engine.getTemplate("<html><head></head><body><h1>POST action with backslash</h1><b>You posting data with a URL with a backslash. Alter the form to post with the same URL without the backslash</body></html>");

                res.setStatus(500);
                res.setContentType("text/html");
                compiledTemplate.evaluate(bufWriter);
            } else {
                errorPage(req, res, bufWriter, sites, 500);
            }
        }
    }

    private static String getFullURL(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null) {
            requestURL.append('?').append(queryString);
        }
        return requestURL.toString();
    }

    private Map<String, Object> makeForUser(User user) {
        return ImmutableMap.of("username", user.getUsername());
    }

    private void renderPage(final HttpServletRequest req, String reqPagePath, HttpServletResponse res, Writer bufWriter,
            Site site, Page page) throws PebbleException, IOException {

        TemplateContext global = new TemplateContext();

        global.put("request", makeRequestWrapper(req));
        global.put("app", makeAppWrapper());
        global.put("site", makeSiteWrapper(site));
        global.put("page", page);
        global.put("staticDir", site.getStaticDirectory());

        List<TemplateContext> components = new ArrayList<TemplateContext>();

        for (Component component : page.getComponentsSet()) {
            TemplateContext local = new TemplateContext();
            component.handle(page, req, local, global);
            components.add(local);
        }

        global.put("components", makePageWrapper(page));

        PebbleTemplate compiledTemplate = engine.getTemplate(site.getTheme().getType() + "/" + page.getTemplate().getFilePath());

        res.setStatus(200);
        res.setContentType("text/html");
        compiledTemplate.evaluate(bufWriter, global);
    }

    private Map<String, Object> makeAppWrapper() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        PortalConfiguration configuration = PortalConfiguration.getInstance();
        result.put("title", configuration.getApplicationTitle());
        result.put("subtitle", configuration.getApplicationSubTitle());
        result.put("copyright", configuration.getApplicationSubTitle());
        return result;
    }

    private Map<String, Object> makeSiteWrapper(Site site) {
        HashMap<String, Object> result = new HashMap<String, Object>();

        result.put("name", site.getName());
        result.put("description", site.getDescription());
        result.put("createdBy", makeForUser(site.getCreatedBy()));
        result.put("creationDate", site.getCreationDate());
        result.put("siteObject", site.getObject());

        return result;
    }

    private Map<String, Object> makePageWrapper(Page page) {
        HashMap<String, Object> result = new HashMap<String, Object>();

        result.put("name", page.getName());
        result.put("user", makeForUser(page.getCreatedBy()));
        result.put("createdBy", makeForUser(page.getCreatedBy()));
        result.put("creationDate", page.getCreationDate());
        return result;
    }

    private Map<String, Object> makeRequestWrapper(HttpServletRequest req) {
        HashMap<String, Object> result = new HashMap<String, Object>();

        if (Authenticate.isLogged()) {
            result.put("user", ImmutableMap.of("username", Authenticate.getUser().getUsername(), "isAuthenticated", true));
        } else {
            result.put("user", ImmutableMap.of("username", "", "isAuthenticated", false));
        }

        result.put("method", req.getMethod());
        result.put("protocol", req.getProtocol());

        result.put("url", getFullURL(req));
        result.put("contentType", req.getContentType());
        result.put("contextPath", req.getContextPath());

        return result;
    }

    private void errorPage(final HttpServletRequest req, HttpServletResponse res, Writer bufWriter, Site site, int errorCode)
            throws ServletException, IOException {
        CMSTheme cmsTheme = site.getTheme();
        if (cmsTheme != null && cmsTheme.definesPath(errorCode + ".html")) {
            try {
                PebbleTemplate compiledTemplate = engine.getTemplate(cmsTheme.getType() + "/" + errorCode + ".html");
                TemplateContext global = new TemplateContext();
                global.put("request", makeRequestWrapper(req));
                global.put("site", makeSiteWrapper(site));
                global.put("staticDir", site.getStaticDirectory());
                res.setStatus(errorCode);
                res.setContentType("text/html");
                compiledTemplate.evaluate(bufWriter, global);
            } catch (PebbleException e) {
                throw new ServletException("Could not render error page for " + errorCode);
            }
        } else {
            res.sendError(errorCode, req.getRequestURI());
        }
    }

    public void invalidateEntry(String key) {
        engine.getTemplateCache().invalidate(key);
    }

}