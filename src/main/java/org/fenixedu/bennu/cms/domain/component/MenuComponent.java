package org.fenixedu.bennu.cms.domain.component;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import org.fenixedu.bennu.cms.domain.Menu;
import org.fenixedu.bennu.cms.domain.MenuItem;
import org.fenixedu.bennu.cms.domain.Page;
import org.fenixedu.bennu.cms.rendering.TemplateContext;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.collect.Lists;

/**
 * Component that provides a {@link Menu} for a given {@link Page}
 */
@ComponentType(type = "menu", name = "Menu", description = "Attaches a Menu to a Page")
public class MenuComponent extends MenuComponent_Base {

    public MenuComponent() {
        setCreatedBy(Authenticate.getUser());
        setCreationDate(new DateTime());
    }

    public MenuComponent(Menu menu) {
        this();
        setMenu(menu);
    }

    public MenuComponent(Menu menu, Page page) {
        this();
        init(menu, page);
    }

    public void init(Menu menu, Page page) {
        setMenu(menu);
        addInstalledPage(page);
    }

    @Override
    public void handle(Page currentPage, TemplateContext local, TemplateContext global) {
        if (!getMenu().getChildrenSorted().isEmpty()) {
            handleMenu(getMenu(), "menus", currentPage, global);
            local.put("menu", menuWrapper(getMenu(), currentPage));
        }
    }

    @SuppressWarnings("unchecked")
    public void handleMenu(Menu menu, String menuType, Page currentPage, TemplateContext global) {
        List<MenuWrapper> menus = (List<MenuWrapper>) global.getOrDefault(menuType, Lists.newArrayList());
        menus.add(menuWrapper(menu, currentPage));
        global.put(menuType, menus);
    }

    public MenuWrapper menuWrapper(Menu menu, Page currentPage) {
        return new MenuWrapper(menu.getName(), menuItemWrappers(menu.getChildrenSorted(), currentPage));
    }

    private static List<MenuItemWrapper> menuItemWrappers(List<MenuItem> menuItems, Page currentPage) {
        return menuItems.stream().map(menuItem -> new MenuItemWrapper(menuItem, currentPage)).collect(toList());
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

    public static class MenuItemWrapper {
        public LocalizedString name;
        public Boolean isFolder;
        public String address;
        public Boolean isActive;
        public Boolean isOpen;
        public List<MenuItemWrapper> children;

        public MenuItemWrapper(MenuItem menuItem, Page currentPage) {
            this.name = menuItem.getName();
            this.isFolder = Optional.ofNullable(menuItem.getFolder()).orElse(false);
            this.address = menuItem.getAddress();
            this.isActive = isActive(menuItem, currentPage);
            this.children = menuItemWrappers(menuItem.getChildrenSorted(), currentPage);
            this.isOpen = isOpen(menuItem, currentPage);
        }

        private boolean isOpen(List<MenuItem> children, Page currentPage) {
            return children.stream().filter(child -> isOpen(child, currentPage)).findAny().isPresent();
        }

        private boolean isOpen(MenuItem menuItem, Page currentPage) {
            return isActive(menuItem, currentPage) || isOpen(menuItem.getChildrenSorted(), currentPage);
        }

        private boolean isActive(MenuItem menuItem, Page currentPage) {
            return menuItem.getPage() != null && menuItem.getPage().equals(currentPage);
        }
    }

    public static class MenuWrapper {
        public LocalizedString name;
        public List<MenuItemWrapper> children;

        public MenuWrapper(LocalizedString name, List<MenuItemWrapper> children) {
            this.name = name;
            this.children = children;
        }
    }
}
