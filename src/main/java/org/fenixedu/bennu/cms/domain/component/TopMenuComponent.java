package org.fenixedu.bennu.cms.domain.component;

import org.fenixedu.bennu.cms.domain.Menu;
import org.fenixedu.bennu.cms.domain.Page;
import org.fenixedu.bennu.cms.domain.wraps.Wrap;
import org.fenixedu.bennu.cms.rendering.TemplateContext;

@ComponentType(name = "Top Menu", description = "Attaches a Top Menu to a Page")
public class TopMenuComponent extends TopMenuComponent_Base {

    @DynamicComponent
    public TopMenuComponent(@ComponentParameter(value = "Menu", provider = MenusForSite.class) Menu menu) {
        super();
        setMenu(menu);
    }

    @Override
    public void handle(Page currentPage, TemplateContext local, TemplateContext global) {
        if (!getMenu().getToplevelItemsSet().isEmpty()) {
            Wrap wrap = getMenu().makeWrap(currentPage);
            local.put("topMenu", wrap);
            handleMenu(wrap, "topMenus", global);
        }
    }
}
