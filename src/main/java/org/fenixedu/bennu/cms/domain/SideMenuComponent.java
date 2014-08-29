package org.fenixedu.bennu.cms.domain;

import org.fenixedu.bennu.cms.rendering.TemplateContext;

@ComponentType(type = "sideMenu", name = "Side Menu", description = "Attaches a Side Menu to a Page")
public class SideMenuComponent extends SideMenuComponent_Base {

    public SideMenuComponent(Menu menu, Page page) {
        super();
        init(menu, page);
    }

    @Override
    public void handle(Page currentPage, TemplateContext local, TemplateContext global) {
        if (!getMenu().getChildrenSorted().isEmpty()) {
            local.put("sideMenu", menuWrapper(getMenu(), currentPage));
            handleMenu(getMenu(), "sideMenus", currentPage, global);
        }
    }

}
