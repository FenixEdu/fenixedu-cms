package org.fenixedu.bennu.cms.domain.component;

import java.util.Objects;

import org.fenixedu.bennu.cms.domain.Page;
import org.fenixedu.bennu.cms.rendering.TemplateContext;
import org.fenixedu.bennu.core.domain.Bennu;

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
    public Class<?> componentType() {
        return getComponent().getClass();
    }

    public static Component forType(Class<? extends CMSComponent> type) {
        return Bennu.getInstance().getCmsComponentsSet().stream()
                .filter(component -> component.getComponent().getClass().equals(type)).findAny().orElseGet(() -> {
                    try {
                        return new StrategyBasedComponent(type.newInstance());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}
