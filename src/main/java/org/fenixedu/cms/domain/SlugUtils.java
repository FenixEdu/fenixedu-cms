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

import java.util.UUID;

import org.fenixedu.commons.StringNormalizer;

import com.google.common.base.Joiner;

public class SlugUtils {
    static String makeSlug(Sluggable element, String slug){
        if (slug == null) {
            slug = "";
        }

        slug = StringNormalizer.slugify(slug);

        while (!element.isValidSlug(slug)) {
            String randomSlug = UUID.randomUUID().toString().substring(0, 3);
            slug = Joiner.on("-").join(slug, randomSlug);
        }
        
        return slug;
    }
}
