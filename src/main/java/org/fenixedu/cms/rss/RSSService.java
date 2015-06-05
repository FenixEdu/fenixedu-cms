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
package org.fenixedu.cms.rss;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.core.Project;
import pt.ist.fenixframework.core.exception.ProjectException;

/**
 * Service that generates an RSS Feed from either a {@link Site} (in which case the feed info is that of the site, and all the
 * site's posts) or a {@link Category} (in which case the feed info is that of the category, and all the category's posts).
 * 
 * The generated RSS is compliant with the <a href="http://cyber.law.harvard.edu/rss/rss.html">RSS 2.0 specification</a>,
 * implementing many of the optional feed elements.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
public class RSSService {

    private static final String CMS_VERSION = getCMSVersion();

    /**
     * Returns the RSS 2.0 feed for the given {@link Site}, containing all the site's posts as items.
     * 
     * @param site
     *            The site to generate the feed for.
     * @param locale
     *            The locale in which to generate the feed.
     * @return
     *         The XML of the feed
     * @throws XMLStreamException
     *             If an exception occurs while generating the feed
     */
    public static String generateRSSForSite(Site site, Locale locale) throws XMLStreamException {
        return generateRSS(site.getRssUrl(), site.getFullUrl(), contentOf(site.getName(), locale),
                contentOf(site.getDescription(), locale), locale, site.getPostSet());
    }

    /**
     * Returns the RSS 2.0 feed for the given {@link Category}, containing all the category's posts as items.
     * 
     * @param category
     *            The category to generate the feed for.
     * @param locale
     *            The locale in which to generate the feed.
     * @return
     *         The XML of the feed
     * @throws XMLStreamException
     *             If an exception occurs while generating the feed
     */
    public static String generateRSSForCategory(Category category, Locale locale) throws XMLStreamException {
        String title = contentOf(category.getName(), locale) + " · " + contentOf(category.getSite().getName(), locale);
        return generateRSS(category.getRssUrl(), category.getAddress(), title, title, locale, category.getPostsSet());
    }

    private static String generateRSS(String rssUrl, String url, String title, String description, Locale locale,
            Collection<Post> posts) throws XMLStreamException {

        StringWriter strWriter = new StringWriter();
        XMLEventWriter writer = XMLOutputFactory.newInstance().createXMLEventWriter(strWriter);
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent nl = eventFactory.createCharacters("\n");

        writer.add(eventFactory.createStartDocument());
        writer.add(nl);

        writer.add(eventFactory.createStartElement("", "", "rss"));
        writer.add(eventFactory.createAttribute("version", "2.0"));
        writer.add(eventFactory.createAttribute("xmlns:atom", "http://www.w3.org/2005/Atom"));
        writer.add(nl);

        writer.add(eventFactory.createStartElement("", "", "channel"));
        writer.add(nl);

        writer.add(eventFactory.createCharacters("\t"));
        writer.add(eventFactory.createStartElement("", "", "atom:link"));
        writer.add(eventFactory.createAttribute("rel", "self"));
        writer.add(eventFactory.createAttribute("type", "application/rss+xml"));
        writer.add(eventFactory.createAttribute("href", rssUrl));
        writer.add(eventFactory.createEndElement("", "", "atom:link"));
        writer.add(nl);

        createNode(writer, eventFactory, "title", title);
        createNode(writer, eventFactory, "link", url);
        createNode(writer, eventFactory, "description", description);
        createNode(writer, eventFactory, "language", locale.toLanguageTag());
        createNode(writer, eventFactory, "copyright",
                contentOf(PortalConfiguration.getInstance().getApplicationCopyright(), locale));
        createNode(writer, eventFactory, "webMaster", PortalConfiguration.getInstance().getSupportEmailAddress() + " ("
                + contentOf(PortalConfiguration.getInstance().getApplicationTitle(), locale) + ")");
        createNode(writer, eventFactory, "generator", "FenixEdu CMS " + CMS_VERSION);
        createNode(writer, eventFactory, "docs", "http://blogs.law.harvard.edu/tech/rss");
        createNode(writer, eventFactory, "ttl", "60");
        writer.add(nl);

        for (Post post : posts) {
            // Is this post public?
            if (post.isVisible() && post.getCanViewGroup().isMember(null)) {
                writePost(locale, writer, post, eventFactory);
            }
        }

        writer.add(eventFactory.createEndElement("", "", "channel"));
        writer.add(nl);
        writer.add(eventFactory.createEndElement("", "", "rss"));
        writer.add(nl);
        writer.add(eventFactory.createEndDocument());
        writer.close();

        return strWriter.toString();
    }

    // Pattern to remove invalid XML characters
    private static final Pattern sanitizeInputForXml = Pattern
            .compile("[^\\u0009\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD\uD800\uDC00-\uDBFF\uDFFF]");

    @SuppressWarnings("deprecation")
    private static void writePost(Locale locale, XMLEventWriter writer, Post post, XMLEventFactory eventFactory)
            throws XMLStreamException {
        writer.add(eventFactory.createStartElement("", "", "item"));
        writer.add(eventFactory.createCharacters("\n"));
        createNode(writer, eventFactory, "title", sanitizeInputForXml.matcher(contentOf(post.getName(), locale)).replaceAll(""));
        createNode(writer, eventFactory, "description", sanitizeInputForXml.matcher(contentOf(post.getBody(), locale))
                .replaceAll(""));
        createNode(writer, eventFactory, "link", post.getAddress());
        boolean hasEmail = post.getCreatedBy().getProfile() != null && post.getCreatedBy().getProfile().getEmail() != null;
        String authorEmail = hasEmail ? post.getCreatedBy().getProfile().getEmail() : "";
        createNode(writer, eventFactory, "author", authorEmail + " (" + post.getCreatedBy().getName() + ")");
        createNode(writer, eventFactory, "guid", post.getAddress() + "#" + post.getExternalId());
        if (!post.getCategoriesSet().isEmpty()) {
            createNode(writer, eventFactory, "category",
                    post.getCategoriesSet().stream().map(cat -> contentOf(cat.getName(), locale)).collect(joining("/")));
        }
        createNode(writer, eventFactory, "pubDate", post.getCreationDate()
                .toString("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH));

        writer.add(eventFactory.createCharacters("\n"));
        writer.add(eventFactory.createEndElement("", "", "item"));
        writer.add(eventFactory.createCharacters("\n"));
    }

    private static String getCMSVersion() {
        try {
            Project project = Project.fromName("fenixedu-cms");
            return project == null ? "" : "v" + project.getVersion();
        } catch (IOException | ProjectException e) {
            return "";
        }
    }

    private static void createNode(XMLEventWriter eventWriter, XMLEventFactory eventFactory, String name, String value)
            throws XMLStreamException {
        eventWriter.add(eventFactory.createCharacters("\t"));
        eventWriter.add(eventFactory.createStartElement("", "", name));
        eventWriter.add(eventFactory.createCharacters(value == null ? "" : value));
        eventWriter.add(eventFactory.createEndElement("", "", name));
        eventWriter.add(eventFactory.createCharacters("\n"));
    }

    private static String contentOf(LocalizedString localizedString, Locale locale) {
        return ofNullable(ofNullable(localizedString.getContent(locale)).orElse(localizedString.getContent())).orElse("");
    }

}
