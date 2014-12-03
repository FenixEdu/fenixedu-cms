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

import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.rendering.TemplateContext;

public interface CMSComponent {

    /**
     * Provides the necessary info needed to render the component on a given page and context.
     * 
     * @param page
     *            the page where the component will be rendered.
     * @param componentContext
     *            local context for the component.
     * @param globalContext
     *            global context where the component is being rendered.
     */
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext);

    public default String externalize() {
        return getClass().getName();
    }

    public static CMSComponent internalize(String name) {
        try {
            Class<?> type = Class.forName(name);
            return (CMSComponent) type.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return (page, local, global) -> {
            };
        }
    }

}
