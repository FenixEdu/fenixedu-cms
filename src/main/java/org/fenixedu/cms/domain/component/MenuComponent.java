package org.fenixedu.cms.domain.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.wraps.Wrap;
import org.fenixedu.cms.rendering.TemplateContext;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

/**
 * Component that provides a {@link Menu} for a given {@link Page}
 */
@ComponentType(name = "Menu", description = "Attaches a Menu to a Page")
public class MenuComponent extends MenuComponent_Base {

    public MenuComponent() {
        setCreatedBy(Authenticate.getUser());
        setCreationDate(new DateTime());
    }

    @DynamicComponent
    public MenuComponent(@ComponentParameter(value = "Menu", provider = MenusForSite.class) Menu menu) {
        this();
        setMenu(menu);
    }

    @Override
    public void handle(Page currentPage, TemplateContext local, TemplateContext global) {
        if (!getMenu().getToplevelItemsSet().isEmpty()) {
            Wrap wrap = getMenu().makeWrap(currentPage);
            handleMenu(wrap, "menus", global);
            local.put("menu", wrap);
        }
    }

    public static class MenusForSite implements ComponentContextProvider<Menu> {
        @Override
        public Collection<Menu> provide(Page page) {
            return page.getSite().getMenusSet();
        }

        @Override
        public String present(Menu menu) {
            return menu.getName().getContent();
        }
    }

    @SuppressWarnings("unchecked")
    public void handleMenu(Wrap menu, String menuType, TemplateContext global) {
        List<Wrap> menus = (List<Wrap>) global.computeIfAbsent(menuType, key -> new ArrayList<>());

        menus.add(menu);
        global.put(menuType, menus);
    }

    @Override
    @Atomic(mode = TxMode.WRITE)
    public void delete() {
        this.setMenu(null);
        super.delete();
    }

    @Override
    public String getName() {
        String name = super.getName();
        return name + " (" + getMenu().getName().getContent() + ")";
    }

}
