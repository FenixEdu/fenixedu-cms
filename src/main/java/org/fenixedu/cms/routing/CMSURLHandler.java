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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.servlet.SemanticURLHandler;
import org.fenixedu.cms.domain.CMSTheme;
import org.fenixedu.cms.domain.CMSThemeFile;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.rendering.CMSExtensions;
import org.fenixedu.cms.rss.RSSService;
import org.fenixedu.commons.i18n.I18N;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public final class CMSURLHandler implements SemanticURLHandler {

    private static final Logger logger = LoggerFactory.getLogger(CMSURLHandler.class);

    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("E, d MMM yyyy HH:mm:ss z");

    private CMSRenderer renderer = new CMSRenderer();

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
        String pageSlug = req.getRequestURI().substring(req.getContextPath().length());
        Site site = getSite(menu, pageSlug);
        if (site == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        res.setCharacterEncoding(Charset.defaultCharset().name());
        handleRequest(site, req, res, pageSlug);
    }

    public void handleRequest(Site site, HttpServletRequest req, HttpServletResponse res, String pageSlug) throws IOException,
            ServletException {

        if (site.getCanViewGroup().isMember(Authenticate.getUser())) {
            if (site.getPublished()) {
                try {
                    String baseUrl = "/" + site.getBaseUrl();
                    if (pageSlug.startsWith(baseUrl)) {
                        pageSlug = pageSlug.substring(baseUrl.length());
                    }
                    if (pageSlug.endsWith("/") && !req.getRequestURI().equals(req.getContextPath() + "/")) {
                        handleLeadingSlash(req, res, site);
                    } else if (pageSlug.startsWith("/static/")) {
                        handleStaticResource(req, res, site, pageSlug);
                    } else if (pageSlug.startsWith("/rss")) {
                        handleRSS(req, res, site, pageSlug);
                    } else {
                        renderer.renderCMSPage(req, res, site, pageSlug);
                    }
                } catch (Exception e) {
                    logger.error("Exception while rendering CMS page " + req.getRequestURI(), e);
                    if (res.isCommitted()) {
                        return;
                    }
                    res.reset();
                    res.resetBuffer();
                    renderer.errorPage(req, res, site, 500);
                }
            } else {
                res.sendError(404);
            }
        } else {
            res.sendError(404);
            return;
        }
    }

    public void invalidateEntry(String key) {
        renderer.invalidateEntry(key);
    }

    private void handleRSS(HttpServletRequest req, HttpServletResponse res, Site site, String slug) throws IOException,
            XMLStreamException, ServletException {
        slug = slug.replaceFirst("/", "");

        Locale locale =
                Strings.isNullOrEmpty(req.getParameter("locale")) ? I18N.getLocale() : new Locale.Builder().setLanguageTag(
                        req.getParameter("locale")).build();

        String[] parts = slug.split("/");

        if (parts.length == 1) {
            res.setContentType("application/rss+xml;charset=UTF-8");
            res.getOutputStream().write(RSSService.generateRSSForSite(site, locale).getBytes(StandardCharsets.UTF_8));
        } else {
            Category category = site.categoryForSlug(parts[1]);
            if (category == null) {
                renderer.errorPage(req, res, site, 404);
            } else {
                res.setContentType("application/rss+xml;charset=UTF-8");
                res.getOutputStream().write(RSSService.generateRSSForCategory(category, locale).getBytes(StandardCharsets.UTF_8));
            }
        }

    }

    private Site getSite(MenuFunctionality menu, String url) {
        if (menu.getSites() != null) {
            return menu.getSites();
        }
        return Optional.ofNullable(menu.getCmsFolder()).map((x) -> {
            return x.resolveSite(url);
        }).orElse(null);
    }

    private void handleStaticResource(final HttpServletRequest req, HttpServletResponse res, Site sites, String pageSlug)
            throws IOException, ServletException {
        pageSlug = pageSlug.replaceFirst("/", "");
        CMSTheme theme = sites.getTheme();
        if (theme != null) {
            byte[] bytes = theme.contentForPath(pageSlug);
            if (bytes != null) {
                CMSThemeFile templateFile = theme.fileForPath(pageSlug);
                String etag =
                        "W/\"" + bytes.length + "-" + (templateFile == null ? "na" : templateFile.getLastModified().getMillis())
                                + "\"";
                res.setHeader("ETag", etag);
                if (etag.equals(req.getHeader("If-None-Match"))) {
                    res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    return;
                }
                res.setHeader("Expires", formatter.print(DateTime.now().plusHours(12)));
                res.setHeader("Cache-Control", "max-age=43200");
                res.setContentLength(bytes.length);
                if (templateFile != null) {
                    res.setContentType(templateFile.getContentType());
                } else {
                    res.setContentType(new MimetypesFileTypeMap().getContentType(pageSlug));
                }
                res.getOutputStream().write(bytes);
                return;
            }
        }
        res.sendError(404);
    }


    private void handleLeadingSlash(final HttpServletRequest req, HttpServletResponse res, Site sites) throws PebbleException,
            IOException, ServletException {
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
                compiledTemplate.evaluate(res.getWriter());
            } else {
                renderer.errorPage(req, res, sites, 500);
            }
        }
    }
}