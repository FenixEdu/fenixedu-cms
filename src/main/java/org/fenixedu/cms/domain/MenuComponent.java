package org.fenixedu.cms.domain;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.cms.rendering.TemplateContext;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

/**
 * Component that provides a {@link Menu} for a given {@link Page}
 */
@ComponentType(type="menu", name="Menu", description="Attaches a Menu to a Page")
public class MenuComponent extends MenuComponent_Base {
    
    public MenuComponent() {
        super();
    }

    @Override
    public void handle(Page page, HttpServletRequest req, TemplateContext local, TemplateContext global) {
        ArrayList<Menu> menus = (ArrayList<Menu>) global.get("menus");
        
        if (menus == null){
            menus = new ArrayList<>();
        }
        
        Menu menu = getMenu();
        menus.add(menu);
        
        local.put("menu", menu);
        global.put("menus", menus);
    }
    
    @Override
    @Atomic(mode=TxMode.WRITE)
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
