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
package org.fenixedu.cms.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.cms.domain.component.Component;

import com.google.gson.JsonElement;

@DefaultJsonAdapter(Component.class)
public class ComponentAdapter implements JsonAdapter<Component> {

    @Override
    public Component create(JsonElement json, JsonBuilder ctx) {
        return null;
    }

    @Override
    public Component update(JsonElement arg0, Component arg1, JsonBuilder arg2) {
        return null;
    }

    @Override
    public JsonElement view(Component component, JsonBuilder ctx) {
        return Component.forType(component.getClass().getName()).toJson();
    }
}
