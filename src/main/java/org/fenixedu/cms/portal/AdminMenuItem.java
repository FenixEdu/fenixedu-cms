package org.fenixedu.cms.portal;

import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.MenuItem;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@Controller
@RequestMapping("/cms/manage")
public class AdminMenuItem {

    @RequestMapping(value = "{slugSite}/menus/{oidMenu}/change", method = RequestMethod.GET)
    public String change(Model model, @PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "oidMenu") String oidMenu) {
        Site s = Site.fromSlug(slugSite);
        model.addAttribute("site", s);
        model.addAttribute("menu", s.menuForOid(oidMenu));
        return "changeMenu";
    }

    private JsonObject serialize(MenuItem item) {
        JsonObject root = new JsonObject();

        root.add("title", new JsonPrimitive(item.getName().getContent()));
        root.add("key", new JsonPrimitive(item.getExternalId()));
        root.add("url", item.getUrl() == null ? null : new JsonPrimitive(item.getUrl()));
        root.add("page", item.getPage() == null ? null : new JsonPrimitive(item.getPage().getSlug()));
        root.add("position", new JsonPrimitive(item.getPosition()));
        
        if (item.getChildrenSet().size() > 0) {
            root.add("folder", new JsonPrimitive(true));
            JsonArray child = new JsonArray();
            
            for (MenuItem subitem : item.getChildrenSorted()) {
                child.add(serialize(subitem));
            }
            root.add("children", child);
        }
        return root;
    }

    @RequestMapping(value = "{slugSite}/menus/{oidMenu}/data", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String data(Model model, @PathVariable(value = "slugSite") String slugSite, @PathVariable(value = "oidMenu") String oidMenu) {
        Site s = Site.fromSlug(slugSite);
        Menu m = s.menuForOid(oidMenu);

        JsonObject root = new JsonObject();

        root.add("title", new JsonPrimitive(m.getName().getContent()));
        root.add("key", new JsonPrimitive("null"));
        root.add("root", new JsonPrimitive(true));
        root.add("folder", new JsonPrimitive(true));

        JsonArray child = new JsonArray();

        for (MenuItem subitem : m.getToplevelItemsSorted()) {
            child.add(serialize(subitem));
        }

        root.add("children", child);

        JsonArray top = new JsonArray();

        top.add(root);
        return top.toString();
    }

    @RequestMapping(value = "{slugSite}/menus/{oidMenu}/createItem", method = RequestMethod.POST)
    public RedirectView create(Model model, @PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "oidMenu") String oidMenu, @RequestParam String menuItemOid, @RequestParam String name,
            @RequestParam String use, @RequestParam String url, @RequestParam String slugPage) {
        Site s = Site.fromSlug(slugSite);
        Menu m = s.menuForOid(oidMenu);

        createMenuItem(menuItemOid, name, use, url, slugPage, s, m);
        return new RedirectView("/cms/manage/" + slugSite + "/menus/" + oidMenu + "/change", true);
    }

    @Atomic
    private void createMenuItem(String menuItemOid, String name, String use, String url, String slugPage, Site s, Menu m) {
        MenuItem mi = new MenuItem();
        mi.setName(new LocalizedString(I18N.getLocale(), name));
        mi.setMenu(m);

        if (!menuItemOid.equals("null")) {
            MenuItem parent = FenixFramework.getDomainObject(menuItemOid);
            if (parent.getMenu() != m) {
                throw new RuntimeException("Wrong Parents");
            }
            parent.add(mi);
        } else {
            m.add(mi);
        }

        if (use.equals("url")) {
            mi.setUrl(url);
        } else {
            Page p = s.pageForSlug(slugPage);
            if (p == null) {
                throw new RuntimeException("Page not found");
            }
            mi.setPage(p);
        }
    }

    @RequestMapping(value = "{slugSite}/menus/{oidMenu}/changeItem", method = RequestMethod.POST)
    public RedirectView change(Model model, @PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "oidMenu") String oidMenu, @RequestParam String menuItemOid,
            @RequestParam String menuItemOidParent, @RequestParam String name, @RequestParam String use,
            @RequestParam String url, @RequestParam String slugPage, @RequestParam Integer position) {

        Site s = Site.fromSlug(slugSite);
        Menu m = s.menuForOid(oidMenu);

        if (menuItemOid.equals("null") && menuItemOidParent.equals("null")) {
            changeMenu(m, name);
        } else {

            MenuItem mi = FenixFramework.getDomainObject(menuItemOid);

            if (mi.getMenu() != m) {
                throw new RuntimeException("Wrong Parents");
            }

            changeMenuItem(mi, menuItemOidParent, name, use, url, slugPage, s, m, position);
        }
        return new RedirectView("/cms/manage/" + slugSite + "/menus/" + oidMenu + "/change", true);
    }

    @Atomic
    private void changeMenu(Menu m, String name) {
        m.setName(new LocalizedString(I18N.getLocale(), name));
    }

    @Atomic
    private void changeMenuItem(MenuItem mi, String menuItemOid, String name, String use, String url, String slugPage, Site s,
            Menu m, Integer position) {

        mi.setName(new LocalizedString(I18N.getLocale(), name));

        if (!menuItemOid.equals("null")) {
            if ((mi.getParent() == null && !menuItemOid.equals("null"))
                    || (mi.getParent() != null && !mi.getParent().getOid().equals(menuItemOid))) {
                MenuItem mip = FenixFramework.getDomainObject(menuItemOid);

                if (mip.getMenu() != m) {
                    throw new RuntimeException("Wrong Parents");
                }
                mip.putAt(mi, position);
                mi.setTop(null);
            }
        } else {
            m.putAt(mi, position);
            mi.setParent(null);
        }

        if (use.equals("url")) {
            mi.setUrl(url);
            mi.setPage(null);
        } else if (use.equals("page")) {
            mi.setUrl(null);
            Page p = s.pageForSlug(slugPage);

            if (p == null) {
                throw new RuntimeException("Page not found");
            }

            mi.setPage(p);
        }
    }

    @RequestMapping(value = "{slugSite}/menus/{oidMenu}/delete/{oidMenuItem}", method = RequestMethod.GET)
    public RedirectView delete(Model model, @PathVariable(value = "slugSite") String slugSite,
            @PathVariable(value = "oidMenu") String oidMenu, @PathVariable(value = "oidMenuItem") String oidMenuItem) {
        Site s = Site.fromSlug(slugSite);
        Menu m = s.menuForOid(oidMenu);
        MenuItem item = FenixFramework.getDomainObject(oidMenuItem);
        if (item.getMenu() != m) {
            throw new RuntimeException("Wrong Parents");
        }
        item.delete();
        return new RedirectView("/cms/manage/" + slugSite + "/menus/" + oidMenu + "/change", true);
    }
}
