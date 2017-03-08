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
package org.fenixedu.cms.domain.component;

import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.wraps.Wrap;
import org.fenixedu.cms.rendering.TemplateContext;

import java.util.stream.Collectors;

/**
 * Component that lists all the {@link Category} of a given site.
 */
@ComponentType(name = "List Categories", description = "List all Categories for this site")
public class ListOfCategories implements CMSComponent {

    public ListOfCategories() {
        super();
    }

    @Override
    public void handle(Page page, TemplateContext local, TemplateContext global) {
        local.put("categories", page.getSite().getCategoriesSet().stream().map(Wrap::make).collect(Collectors.toList()));
        global.put("categories", page.getSite().getCategoriesSet().stream().map(Wrap::make).collect(Collectors.toList()));
    }

}
