package org.fenixedu.bennu.cms.domain;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.cms.rendering.TemplateContext;

/**
 * Component that lists all the {@link Category} of a given site.
 */
@ComponentType(type = "listCategories", name = "List Categories", description = "List all Categories for this site")
public class ListOfCategories extends ListOfCategories_Base {

    public ListOfCategories() {
        super();
    }

    @Override
    public void handle(Page page, HttpServletRequest req, TemplateContext local, TemplateContext global) {
        local.put("categories", page.getSite().getCategoriesSet());
        global.put("categories", page.getSite().getCategoriesSet());
    }

}
