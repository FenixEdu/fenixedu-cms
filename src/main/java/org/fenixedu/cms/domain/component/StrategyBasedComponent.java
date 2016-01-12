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

import java.util.Objects;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.cms.domain.CloneCache;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.rendering.TemplateContext;

import com.google.gson.JsonObject;

public final class StrategyBasedComponent extends StrategyBasedComponent_Base {

    private StrategyBasedComponent(CMSComponent component) {
        super();
        setBennu(Bennu.getInstance());
        setComponent(Objects.requireNonNull(component));
    }

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        getComponent().handle(page, componentContext, globalContext);
    }

    @Override
    public StrategyBasedComponent clone(CloneCache cloneCache) {
        return this;
    }

    @Override
    public Class<?> componentType() {
        return getComponent().getClass();
    }

    @Override
    public JsonObject json() {
        JsonObject json = new JsonObject();
        json.addProperty("type", getType());
        return json;
    }

    static Component componentForType(Class<? extends CMSComponent> type) {
        return Bennu.getInstance().getCmsComponentsSet().stream()
                .filter(component -> component.getComponent().getClass().equals(type)).findAny().orElseGet(() -> {
                    try {
                        return new StrategyBasedComponent(type.newInstance());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public void delete() {
    }

}
