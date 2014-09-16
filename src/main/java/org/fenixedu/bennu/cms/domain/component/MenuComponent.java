package org.fenixedu.bennu.cms.domain.component;

import java.util.Collection;
import java.util.List;

import org.fenixedu.bennu.cms.domain.Menu;
import org.fenixedu.bennu.cms.domain.Page;
import org.fenixedu.bennu.cms.domain.Site;
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

    public static class MenuItemWrapper {
        public LocalizedString name;
        public Boolean isFolder;
        public String address;
        public Boolean isActive;
        public Boolean isOpen;
        public Boolean isVisible;
        public List<MenuItemWrapper> children;

        public MenuItemWrapper(MenuItem menuItem, Page currentPage) {
            this.name = menuItem.getName();
            this.isFolder = Optional.ofNullable(menuItem.getFolder()).orElse(false);
            this.address = menuItem.getAddress();
            this.isActive = isActive(menuItem, currentPage);
            this.children = menuItemWrappers(menuItem.getChildrenSorted(), currentPage);
            this.isOpen = isOpen(menuItem, currentPage);
            this.isVisible =
                    menuItem.getPage() != null && menuItem.getPage().isPublished() || menuItem.getPage() == null
                            && !menuItem.getChildrenSet().isEmpty();
        }

        private static boolean isOpen(List<MenuItem> children, Page currentPage) {
            return children.stream().filter(child -> isOpen(child, currentPage)).findAny().isPresent();
        }

        private static boolean isOpen(MenuItem menuItem, Page currentPage) {
            return isActive(menuItem, currentPage) || isOpen(menuItem.getChildrenSorted(), currentPage);
        }

        private static boolean isActive(MenuItem menuItem, Page currentPage) {
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

    public static boolean supportsSite(Site site) {
        return !site.getMenusSet().isEmpty();
    }

}
