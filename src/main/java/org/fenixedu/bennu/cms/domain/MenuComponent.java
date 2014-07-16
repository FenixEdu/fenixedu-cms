package org.fenixedu.bennu.cms.domain;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.cms.rendering.TemplateContext;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.collect.Lists;

/**
 * Component that provides a {@link Menu} for a given {@link Page}
 */
@ComponentType(type = "menu", name = "Menu", description = "Attaches a Menu to a Page")
public class MenuComponent extends MenuComponent_Base {

    @Override
    public void handle(Page page, HttpServletRequest req, TemplateContext local, TemplateContext global) {
        ArrayList<Menu> menus = (ArrayList<Menu>) global.get("menus");

        if (menus == null) {
            menus = new ArrayList<>();
        }

        Menu menu = getMenu();
        menus.add(menu);

        local.put("menu", menu);
        global.put("menus", menus);
        global.put("menuItemsOpen", open(menus, page));
    }

    private List<MenuItem> open(List<Menu> menus, Page currentPage) {
        List<MenuItem> items = Lists.newArrayList();
        for (Menu menu : menus) {
            items.addAll(openItems(menu.getChildrenSorted(), currentPage));
        }
        return items;
    }

    private List<MenuItem> openItems(List<MenuItem> items, Page currentPage) {
        for (MenuItem child : items) {
            List<MenuItem> openItems = openItems(child, currentPage);
            if (!openItems.isEmpty()) {
                return openItems;
            }
        }
        return Lists.newArrayList();
    }

    private List<MenuItem> openItems(MenuItem item, Page currentPage) {
        if (item.getPage().equals(currentPage)) {
            return Lists.newArrayList(item);
        } else {
            List<MenuItem> openItems = openItems(item.getChildrenSorted(), currentPage);
            if (!openItems.isEmpty()) {
                openItems.add(item);
            }
            return openItems;
        }
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
