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

import java.util.HashMap;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.fenixedu.cms.rendering.TemplateContext;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.gson.JsonArray;

public abstract class Component extends Component_Base {

    protected static final HashMap<String, ComponentDescriptor> COMPONENTS = new HashMap<>();

    /**
     * Registers a new class has a component.
     * 
     * @param c
     *            the class being registered as a component.
     */
    public static void register(Class<?> c) {
        COMPONENTS.put(c.getName(), new ComponentDescriptor(c));
    }

    /**
     * Searches for the class of a component with a given type.
     * 
     * @param type
     *            the type of the component.
     * @return
     *         the class of the component with the given type.
     */
    public static ComponentDescriptor forType(String type) {
        return COMPONENTS.get(type);
    }

    public static JsonArray availableComponents(Site site) {
        JsonArray array = new JsonArray();
        COMPONENTS.values().stream().filter(descriptor -> descriptor.isForSite(site)).map(ComponentDescriptor::toJson)
                .forEach(obj -> array.add(obj));
        return array;
    }

    /**
     * The logged {@link User} creates a new component.
     */
    public Component() {
        super();
        if (Authenticate.getUser() == null) {
            throw CmsDomainException.forbiden();
        }
        this.setCreatedBy(Authenticate.getUser());
        this.setCreationDate(new DateTime());

    }

    /**
     * 
     * @return
     *         the name of the component.
     */
    public String getName() {
        return componentType().getAnnotation(ComponentType.class).name();
    }

    /**
     * 
     * @return
     *         the description of the component.
     */
    public String getDescription() {
        return componentType().getAnnotation(ComponentType.class).description();
    }

    /**
     * @return
     *         the type of the component.
     */
    @Override
    public String getType() {
        return componentType().getName();
    }

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
    public abstract void handle(Page page, TemplateContext componentContext, TemplateContext globalContext);

    @Atomic(mode = TxMode.WRITE)
    public void delete() {
        this.setCreatedBy(null);
        for (Page page : getInstalledPageSet()) {
            page.removeComponents(this);
        }
        this.deleteDomainObject();
    }

    public Class<?> componentType() {
        return this.getClass();
    }

    public static Component forType(Class<? extends CMSComponent> type) {
        return StrategyBasedComponent.componentForType(type);
    }

}
