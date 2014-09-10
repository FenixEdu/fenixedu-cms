package org.fenixedu.bennu.cms.domain.component;

import java.util.Collection;
import java.util.List;

import org.fenixedu.bennu.cms.domain.Menu;
import org.fenixedu.bennu.cms.domain.Page;
import org.fenixedu.bennu.cms.rendering.TemplateContext;
import org.fenixedu.bennu.cms.domain.wraps.Wrap;
import org.fenixedu.bennu.core.security.Authenticate;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.collect.Lists;

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
        if (!getMenu().getChildrenSorted().isEmpty()) {
            handleMenu(getMenu(), "menus", currentPage, global);
            local.put("menu", getMenu().makeWrap(currentPage));
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
    public void handleMenu(Menu menu, String menuType, Page currentPage, TemplateContext global) {
        List<Wrap> menus = (List<Wrap>) global.getOrDefault(menuType, Lists.newArrayList());

        menus.add(menu.makeWrap(currentPage));
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
