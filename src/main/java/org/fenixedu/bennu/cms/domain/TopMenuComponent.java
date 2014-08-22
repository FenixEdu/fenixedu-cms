package org.fenixedu.bennu.cms.domain;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.cms.rendering.TemplateContext;

@ComponentType(type = "topMenu", name = "Top Menu", description = "Attaches a Top Menu to a Page")
public class TopMenuComponent extends TopMenuComponent_Base {

    public TopMenuComponent(Menu menu, Page page) {
        super();
        init(menu, page);
    }

    @Override
    public void handle(Page currentPage, HttpServletRequest req, TemplateContext local, TemplateContext global) {
        if (!getMenu().getChildrenSorted().isEmpty()) {
            local.put("topMenu", menuWrapper(getMenu(), currentPage));
            handleMenu(getMenu(), "topMenus", currentPage, global);
        }
    }
}
