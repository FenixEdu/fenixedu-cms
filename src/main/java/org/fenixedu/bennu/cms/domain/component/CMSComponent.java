package org.fenixedu.bennu.cms.domain.component;

import org.fenixedu.bennu.cms.domain.Page;
import org.fenixedu.bennu.cms.rendering.TemplateContext;

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
