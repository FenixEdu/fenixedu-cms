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
package org.fenixedu.cms.domain;

import org.fenixedu.commons.i18n.LocalizedString;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import java.util.Locale;
import java.util.function.Function;

public class Sanitization {

    private static PolicyFactory CMS_SANITIZER = new HtmlPolicyBuilder()
            .allowStyling()
            .allowStandardUrlProtocols()
            .allowElements("a", "b", "blockquote", "br", "caption", "cite", "code", "col", "colgroup", "dd", "dl", "dt", "em",
                    "h1", "h2", "h3", "h4", "h5", "h6", "i", "img", "li", "ol", "p", "pre", "q", "small", "strike", "strong",
                    "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u", "ul", "div", "font", "span")
            .allowAttributes("href").onElements("a").allowAttributes("title").onElements("a").allowAttributes("cite")
            .onElements("blockquote").allowAttributes("span").onElements("col").allowAttributes("width").onElements("col")
            .allowAttributes("span").onElements("colgroup").allowAttributes("width").onElements("colgroup")
            .allowAttributes("align").onElements("img").allowAttributes("alt").onElements("img").allowAttributes("height")
            .onElements("img").allowAttributes("src").onElements("img").allowAttributes("title").onElements("img")
            .allowAttributes("width").onElements("img").allowAttributes("start").onElements("ol").allowAttributes("type")
            .onElements("ol").allowAttributes("cite").onElements("q").allowAttributes("summary").onElements("table")
            .allowAttributes("width").onElements("table").allowAttributes("abbr").onElements("td").allowAttributes("axis")
            .onElements("td").allowAttributes("colspan").onElements("td").allowAttributes("rowspan").onElements("td")
            .allowAttributes("width").onElements("td").allowAttributes("abbr").onElements("th").allowAttributes("axis")
            .onElements("th").allowAttributes("colspan").onElements("th").allowAttributes("rowspan").onElements("th")
            .allowAttributes("scope").onElements("th").allowAttributes("width").onElements("th").allowAttributes("type")
            .onElements("ul").allowAttributes("class", "color").globally().toFactory();

    private static PolicyFactory STRICT_SANITIZER = new HtmlPolicyBuilder().toFactory();

    public static LocalizedString sanitize(LocalizedString origin) {
        LocalizedString result = new LocalizedString();
        for (Locale l : origin.getLocales()) {
            result = result.with(l, sanitize(origin.getContent(l)));
        }
        return result;
    }

    public static LocalizedString strictSanitize(LocalizedString origin) {
        LocalizedString result = new LocalizedString();
        for (Locale l : origin.getLocales()) {
            result = result.with(l, sanitize(origin.getContent(l)));
        }
        return result;
    }

    public static String sanitize(String original) {
        return CMS_SANITIZER.sanitize(original);
    }

    public static String strictSanitize(String original) {
        return STRICT_SANITIZER.sanitize(original);
    }

}
