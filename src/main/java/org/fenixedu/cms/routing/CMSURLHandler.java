package org.fenixedu.cms.routing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.servlet.SemanticURLHandler;
import org.fenixedu.cms.domain.CMSTemplateFile;
import org.fenixedu.cms.domain.CMSTheme;
import org.fenixedu.cms.domain.Component;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.exceptions.ResourceNotFoundException;
import org.fenixedu.cms.rendering.CMSExtensions;
import org.fenixedu.cms.rendering.TemplateContext;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

final class CMSURLHandler implements SemanticURLHandler {

    public static String rewritePageUrl(HttpServletRequest request) {
        String requestURL = request.getRequestURL().toString();
        String queryString = request.getQueryString();

        if (requestURL.endsWith("/")) {
            requestURL = requestURL.substring(0, requestURL.length() - 1);
        }

        if (queryString == null) {
            return requestURL;
        } else {
            return requestURL + "?" + queryString;
        }
    }

    @Override
    public void handleRequest(MenuFunctionality menu, final HttpServletRequest req, HttpServletResponse res, FilterChain fc)
            throws IOException, ServletException {
        Site sites = menu.getSites();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        Writer bufWriter = new OutputStreamWriter(buf);

        try {
            String pageSlug = req.getRequestURI().substring(req.getContextPath().length() + menu.getFullPath().length());

            if (pageSlug.length() == 0) {

            }

            if (pageSlug.endsWith("/")) {
                if (req.getMethod().equals("GET")) {
                    res.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                    res.setHeader("Location", rewritePageUrl(req));
                    return;
                } else if (req.getMethod().equals("POST")) {
                    PebbleEngine engine = new PebbleEngine(new StringLoader());
                    engine.addExtension(new CMSExtensions());
                    PebbleTemplate compiledTemplate =
                            engine.compile("<html><head></head><body><h1>POST action with backslash</h1><b>You posting data with a URL with a backslash. Alter the form to post with the same URL without the backslash</body></html>");

                    res.setStatus(500);
                    res.setContentType("text/html");
                    compiledTemplate.evaluate(bufWriter);
                }
            }

            if (sites.getTheme() == null) {
                render404(req, pageSlug, res, bufWriter, sites);
                return;
            }

            if (pageSlug.startsWith("/static/")) {
                pageSlug = pageSlug.substring(1);
                CMSTemplateFile file = sites.getTheme().fileForPath(pageSlug);
                if (file != null) {
                    InputStream i = file.getStream();
                    OutputStream o = buf;
                    long l = ByteStreams.copy(i, o);
                    res.setContentLength((int) l);
                    res.setContentType(file.getContentType());
                } else {
                    render404(req, pageSlug, res, bufWriter, sites);
                }
            } else {
                pageSlug = pageSlug.replace("/", "");

                Page page = sites.pageForSlug(pageSlug);

                if (page == null || page.getTemplate() == null) {
                    render404(req, pageSlug, res, bufWriter, sites);
                } else {
                    try {
                        renderPage(req, pageSlug, res, bufWriter, sites, page);
                    } catch (ResourceNotFoundException e) {
                        render404(req, pageSlug, res, bufWriter, sites);
                    }
                }
            }
        } catch (Exception e) {
            buf = new ByteArrayOutputStream();
            bufWriter = new OutputStreamWriter(buf);
            e.printStackTrace();
            try {
                render500(req, res, bufWriter, sites);
            } catch (PebbleException e1) {
                System.out.println("FATAL");
                e1.printStackTrace();
            }
        }

        OutputStream os = res.getOutputStream();
        os.write(buf.toByteArray(), 0, buf.size());
    }

    private static String getFullURL(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    private Map makeForUser(User user) {
        return ImmutableMap.of("username", user.getUsername());
    }

    private void renderPage(final HttpServletRequest req, String reqPagePath, HttpServletResponse res, Writer bufWriter,
            Site site, Page page) throws PebbleException, IOException {

        TemplateContext global = new TemplateContext();

        global.put("request", makeRequestWrapper(req));
        global.put("app", makeAppWrapper());
        global.put("site", makeSiteWrapper(site));
        global.put("page", makePageWrapper(page));
        global.put("staticDir", site.getStaticDirectory());
        
        List<TemplateContext> components = new ArrayList<TemplateContext>();

        for (Component component : page.getComponentsSet()) {
            TemplateContext local = new TemplateContext();
            component.handle(page, req, local, global);
            components.add(local);
        }

        global.put("components", makePageWrapper(page));

        PebbleEngine engine = new PebbleEngine(new CMSTemplateLoader(site));
        engine.addExtension(new CMSExtensions());
        PebbleTemplate compiledTemplate = null;

        compiledTemplate = engine.compile(page.getTemplate().getFile().getDisplayName());

        res.setStatus(200);
        res.setContentType("text/html");
        compiledTemplate.evaluate(bufWriter, global);
    }

    private Map<String, Object> makeAppWrapper() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        Bennu instance = Bennu.getInstance();
        result.put("title", instance.getConfiguration().getApplicationTitle());
        result.put("subtitle", instance.getConfiguration().getApplicationSubTitle());
        result.put("copyright", instance.getConfiguration().getApplicationSubTitle());
        return result;
    }

    private Map<String, Object> makeSiteWrapper(Site site) {
        HashMap<String, Object> result = new HashMap<String, Object>();

        result.put("name", site.getName());
        result.put("description", site.getDescription());
        result.put("createdBy", makeForUser(site.getCreatedBy()));
        result.put("creationDate", site.getCreationDate());

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
            User user = Authenticate.getUser();
            result.put("user", ImmutableMap.of("username", user.getUsername(), "isAuthenticated", true));
        } else {
            result.put("user", ImmutableMap.of("username", "", "isAuthenticated", false));
        }

        result.put("method", req.getMethod());
        result.put("protocol", req.getProtocol());

        result.put("url", getFullURL(req));
        result.put("contentType", req.getContentType());

        return result;
    }

    private void render404(final HttpServletRequest req, String reqPagePath, HttpServletResponse res, Writer bufWriter, Site site)
            throws PebbleException, IOException {
        TemplateContext global = new TemplateContext();
        global.put("request", makeRequestWrapper(req));
        global.put("site", makeSiteWrapper(site));
        global.put("staticDir", site.getStaticDirectory());
        CMSTheme cmsTheme = site.getTheme();

        PebbleTemplate compiledTemplate = null;

        if (cmsTheme != null) {
            PebbleEngine engine = new PebbleEngine(new CMSTemplateLoader(cmsTheme));
            engine.addExtension(new CMSExtensions());
            compiledTemplate = engine.compile("404.html");
        }

        if (cmsTheme == null || compiledTemplate == null) {
            PebbleEngine engine = new PebbleEngine(new StringLoader());
            engine.addExtension(new CMSExtensions());
            compiledTemplate = engine.compile("<html><head></head><body><h1>File Not Found</h1><b>Url:</b>{{url}}</body></html>");
        }

        res.setStatus(404);
        res.setContentType("text/html");
        compiledTemplate.evaluate(bufWriter, global);

    }

    private void render500(final HttpServletRequest req, HttpServletResponse res, Writer bufWriter, Site site)
            throws PebbleException, IOException {
        TemplateContext global = new TemplateContext();
        global.put("request", makeRequestWrapper(req));
        global.put("site", makeSiteWrapper(site));
        global.put("staticDir", site.getStaticDirectory());
        CMSTheme cmsTheme = site.getTheme();

        PebbleTemplate compiledTemplate = null;

        if (cmsTheme != null) {
            PebbleEngine engine = new PebbleEngine(new CMSTemplateLoader(cmsTheme));
            engine.addExtension(new CMSExtensions());
            compiledTemplate = engine.compile("500.html");
        }

        if (cmsTheme == null || compiledTemplate == null) {
            PebbleEngine engine = new PebbleEngine(new StringLoader());
            engine.addExtension(new CMSExtensions());
            compiledTemplate =
                    engine.compile("<html><head></head><body><h1>Internal Server Error</h1><b>Url:</b>{{url}}</body></html>");
        }

        res.setStatus(500);
        res.setContentType("text/html");
        compiledTemplate.evaluate(bufWriter, global);
    }
}