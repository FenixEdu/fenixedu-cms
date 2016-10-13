/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of FenixEdu CMS.
 *
 * FenixEdu CMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu CMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu CMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.cms.routing;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.cms.CMSConfigurationManager;
import org.fenixedu.cms.domain.CMSTheme;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.wraps.UserWrap;
import org.fenixedu.cms.exceptions.ResourceNotFoundException;
import org.fenixedu.cms.rendering.CMSExtensions;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.commons.i18n.I18N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Created by diutsu on 01/03/16.
 */
public class CMSRenderer {

    public interface RenderingPageHandler extends BiConsumer<Page, TemplateContext>{}

    private static final Logger logger = LoggerFactory.getLogger(CMSRenderer.class);

    private static final Set<RenderingPageHandler> HANDLERS = new HashSet<>();

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

    public CMSRenderer() {
        engine.addExtension(new CMSExtensions());
        if (CMSConfigurationManager.isInThemeDevelopmentMode()) {
            engine.setTemplateCache(null);
            logger.info("CMS Theme Development Mode enabled!");
        } else {
            engine.setTemplateCache(CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build());
        }
    }

    void renderCMSPage(final HttpServletRequest req, HttpServletResponse res, Site sites, String pageSlug)
            throws ServletException, IOException, PebbleException {
        if (pageSlug.startsWith("/")) {
            pageSlug = pageSlug.substring(1);
        }
        String[] parts = pageSlug.split("/");

        String pageName = parts[0];

        Page page;
        if (Strings.isNullOrEmpty(pageName) && sites.getInitialPage() != null) {
            page = sites.getInitialPage();
        } else {
            page = sites.getPagesSet().stream().filter(p -> pageName.equals(p.getSlug())).findAny().orElse(null);
        }

        if (page == null || page.getTemplate() == null) {
            errorPage(req, res, sites, 404);
        } else if (!page.getPublished() || !page.getCanViewGroup().isMember(Authenticate.getUser())) {
            if (Authenticate.isLogged()) {
                errorPage(req, res, sites, 403);
            } else {
                errorPage(req, res, sites, 401);
            }
        } else {
            try {
                renderPage(req, pageSlug, res, sites, page, parts);
            } catch (ResourceNotFoundException e) {
                errorPage(req, res, sites, 404);
            }
        }
    }

    private void renderPage(final HttpServletRequest req, String reqPagePath, HttpServletResponse res, Site site, Page page,
                            String[] requestContext) throws PebbleException, IOException {

        TemplateContext global = new TemplateContext();
        global.setRequestContext(requestContext);
        for (String key : req.getParameterMap().keySet()) {
            global.put(key, req.getParameter(key));
        }

        global.put("page", makePageWrapper(page));
        populateSiteInfo(req, page, site, global);

        List<TemplateContext> components = new ArrayList<TemplateContext>();

        for (Component component : page.getComponentsSet()) {
            TemplateContext local = new TemplateContext();
            component.handle(page, local, global);
            components.add(local);
        }

        global.put("components", components);

        for (RenderingPageHandler handler : HANDLERS){
            handler.accept(page,global);
        }

        CMSTheme theme = site.getTheme();

        PebbleTemplate compiledTemplate = engine.getTemplate(theme.getType() + "/" + page.getTemplate().getFilePath());

        res.setStatus(200);
        res.setContentType("text/html");
        compiledTemplate.evaluate(res.getWriter(), global);
    }

    private void populateSiteInfo(final HttpServletRequest req, Page page, Site site, TemplateContext context) {
        context.put("request", makeRequestWrapper(req));
        context.put("app", makeAppWrapper());
        context.put("site", site.makeWrap());
        context.put("menus", makeMenuWrapper(site, page));
        context.put("staticDir", site.getStaticDirectory());
        context.put("devMode", CoreConfiguration.getConfiguration().developmentMode());
    }

    private List<Object> makeMenuWrapper(Site site, Page page) {
        ArrayList<Object> result =
            site.getOrderedMenusSet().stream().map(menu -> page == null ? menu.makeWrap() : menu.makeWrap(page))
                .collect(Collectors.toCollection(ArrayList::new));
        return result;
    }

    private Map<String, Object> makeAppWrapper() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        PortalConfiguration configuration = PortalConfiguration.getInstance();
        result.put("title", configuration.getApplicationTitle());
        result.put("subtitle", configuration.getApplicationSubTitle());
        result.put("copyright", configuration.getApplicationCopyright());
        result.put("support", configuration.getSupportEmailAddress());
        result.put("locale", I18N.getLocale());
        result.put("supportedLocales", CoreConfiguration.supportedLocales());
        return result;
    }

    private Map<String, Object> makePageWrapper(Page page) {
        HashMap<String, Object> result = new HashMap<String, Object>();

        result.put("name", page.getName());
        result.put("user", new UserWrap(page.getCreatedBy()));
        result.put("createdBy", new UserWrap(page.getCreatedBy()));
        result.put("creationDate", page.getCreationDate());
        return result;
    }

    private Map<String, Object> makeRequestWrapper(HttpServletRequest req) {
        HashMap<String, Object> result = new HashMap<String, Object>();

        result.put("user", new UserWrap(Authenticate.getUser()));

        result.put("method", req.getMethod());
        result.put("protocol", req.getProtocol());

        result.put("url", getFullURL(req));
        result.put("contentType", req.getContentType());
        result.put("contextPath", req.getContextPath());

        return result;
    }

    void errorPage(final HttpServletRequest req, HttpServletResponse res, Site site, int errorCode)
            throws ServletException, IOException {
        CMSTheme cmsTheme = site.getTheme();
        if (cmsTheme != null && cmsTheme.definesPath(errorCode + ".html")) {
            try {
                PebbleTemplate compiledTemplate = engine.getTemplate(cmsTheme.getType() + "/" + errorCode + ".html");
                TemplateContext global = new TemplateContext();
                populateSiteInfo(req, null, site, global);
                res.setStatus(errorCode);
                res.setContentType("text/html");
                compiledTemplate.evaluate(res.getWriter(), global);
            } catch (PebbleException e) {
                throw new ServletException("Could not render error page for " + errorCode);
            }
        } else {
            res.sendError(errorCode, req.getRequestURI());
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

    public void invalidateEntry(String key) {
        engine.getTemplateCache().invalidate(key);
    }

    public static void addHandler(RenderingPageHandler handler){
        HANDLERS.add(handler);
    }

    public static void removeHandler(RenderingPageHandler handler){
        HANDLERS.remove(handler);
    }
}
