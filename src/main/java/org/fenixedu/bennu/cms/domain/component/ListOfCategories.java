package org.fenixedu.bennu.cms.domain.component;

import java.util.stream.Collectors;

import org.fenixedu.bennu.cms.domain.Category;
import org.fenixedu.bennu.cms.domain.Page;
import org.fenixedu.bennu.cms.domain.wraps.Wrap;
import org.fenixedu.bennu.cms.rendering.TemplateContext;

/**
 * Component that lists all the {@link Category} of a given site.
 */
@ComponentType(name = "List Categories", description = "List all Categories for this site")
public class ListOfCategories implements CMSComponent {

    public ListOfCategories() {
        super();
    }

    @Override
    public void handle(Page page, TemplateContext local, TemplateContext global) {
        local.put("categories", page.getSite().getCategoriesSet().stream().map(Wrap::make).collect(Collectors.toList()));
        global.put("categories", page.getSite().getCategoriesSet().stream().map(Wrap::make).collect(Collectors.toList()));
    }

}
