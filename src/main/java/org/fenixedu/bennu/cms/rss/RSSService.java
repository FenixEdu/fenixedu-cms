package org.fenixedu.bennu.cms.rss;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.fenixedu.bennu.cms.domain.Category;
import org.fenixedu.bennu.cms.domain.Post;
import org.fenixedu.bennu.cms.domain.Site;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;

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
        return generateRSS(site.getRssUrl(), site.getFullUrl(), site.getName().getContent(locale), site.getDescription()
                .getContent(locale), locale, site.getPostSet());
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
        String title = category.getName().getContent(locale) + " · " + category.getSite().getName().getContent(locale);
        return generateRSS(category.getRssUrl(), category.getAddress(), title, title, locale, category.getPostsSet());
    }

    private static String generateRSS(String rssUrl, String url, String title, String description, Locale locale,
            Collection<Post> posts) throws XMLStreamException {

        StringWriter strWriter = new StringWriter();
        XMLEventWriter writer = XMLOutputFactory.newInstance().createXMLEventWriter(strWriter);
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent nl = eventFactory.createDTD("\n");

        writer.add(eventFactory.createStartDocument());
        writer.add(nl);

        writer.add(eventFactory.createStartElement("", "", "rss"));
        writer.add(eventFactory.createAttribute("version", "2.0"));
        writer.add(eventFactory.createAttribute("xmlns:atom", "http://www.w3.org/2005/Atom"));
        writer.add(nl);

        writer.add(eventFactory.createStartElement("", "", "channel"));
        writer.add(nl);

        writer.add(eventFactory.createDTD("\t"));
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
                PortalConfiguration.getInstance().getApplicationCopyright().getContent(locale));
        createNode(writer, eventFactory, "webMaster", PortalConfiguration.getInstance().getSupportEmailAddress() + " ("
                + PortalConfiguration.getInstance().getApplicationTitle().getContent(locale) + ")");
        createNode(writer, eventFactory, "generator", "FenixEdu CMS " + CMS_VERSION);
        createNode(writer, eventFactory, "docs", "http://blogs.law.harvard.edu/tech/rss");
        createNode(writer, eventFactory, "ttl", "60");
        writer.add(nl);

        for (Post post : posts) {
            writePost(locale, writer, post, eventFactory);
        }

        writer.add(eventFactory.createEndElement("", "", "channel"));
        writer.add(nl);
        writer.add(eventFactory.createEndElement("", "", "rss"));
        writer.add(nl);
        writer.add(eventFactory.createEndDocument());
        writer.close();

        return strWriter.toString();
    }

    private static void writePost(Locale locale, XMLEventWriter writer, Post post, XMLEventFactory eventFactory)
            throws XMLStreamException {
        writer.add(eventFactory.createStartElement("", "", "item"));
        writer.add(eventFactory.createDTD("\n"));
        createNode(writer, eventFactory, "title", post.getName().getContent(locale));
        createNode(writer, eventFactory, "description", post.getBody().getContent(locale));
        createNode(writer, eventFactory, "link", post.getAddress());
        createNode(writer, eventFactory, "author", post.getCreatedBy().getProfile().getEmail() + " ("
                + post.getCreatedBy().getName() + ")");
        createNode(writer, eventFactory, "guid", post.getAddress() + "#" + post.getExternalId());
        if (!post.getCategoriesSet().isEmpty()) {
            createNode(writer, eventFactory, "category",
                    post.getCategoriesSet().stream().map(cat -> cat.getName().getContent(locale))
                            .collect(Collectors.joining("/")));
        }
        createNode(writer, eventFactory, "pubDate", post.getCreationDate()
                .toString("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH));

        writer.add(eventFactory.createDTD("\n"));
        writer.add(eventFactory.createEndElement("", "", "item"));
        writer.add(eventFactory.createDTD("\n"));
    }

    private static String getCMSVersion() {
        try {
            Project project = Project.fromName("bennu-cms");
            return project == null ? "" : "v" + project.getVersion();
        } catch (IOException | ProjectException e) {
            return "";
        }
    }

    private static void createNode(XMLEventWriter eventWriter, XMLEventFactory eventFactory, String name, String value)
            throws XMLStreamException {
        eventWriter.add(eventFactory.createDTD("\t"));
        eventWriter.add(eventFactory.createStartElement("", "", name));
        eventWriter.add(eventFactory.createCharacters(value));
        eventWriter.add(eventFactory.createEndElement("", "", name));
        eventWriter.add(eventFactory.createDTD("\n"));
    }

}
