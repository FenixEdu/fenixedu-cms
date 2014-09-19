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
package org.fenixedu.cms.rendering;

import java.util.HashMap;

public class TemplateContext extends HashMap<String, Object> {

    private static final String REQUEST_CONTEXT_ATTR = "__request_ctx__";
    private static final long serialVersionUID = -2684602340841158526L;

    public void setRequestContext(String[] ctx) {
        put(REQUEST_CONTEXT_ATTR, ctx);
    }

    public String[] getRequestContext() {
        return (String[]) get(REQUEST_CONTEXT_ATTR);
    }

    public String getParameter(String name) {
        return (String) get(name);
    }

}
