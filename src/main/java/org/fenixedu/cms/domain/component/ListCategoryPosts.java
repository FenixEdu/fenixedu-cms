/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of FenixEdu CMS.
 *
 * FenixEdu CMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu CMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu CMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.cms.domain.component;

import com.google.gson.JsonObject;
import org.fenixedu.cms.domain.*;
import org.fenixedu.cms.domain.wraps.Wrap;
import org.fenixedu.cms.rendering.TemplateContext;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Component that lists the {@link Post} of a given category.
 */
@ComponentType(name = "List Category Posts", description = "Lists the Posts from a given category")
public class ListCategoryPosts extends ListCategoryPosts_Base {

    private static final int POSTS_PER_PAGE = 5;

    @DynamicComponent
    public ListCategoryPosts(
            @ComponentParameter(provider = CategoriesForSite.class, value = "Category", required = false) Category cat) {
        setCategory(cat);
    }

    @DynamicComponent
    private ListCategoryPosts(JsonObject json) {
        this(Site.fromSlug(json.get("site").getAsString()).categoryForSlug(json.get("category").getAsString()));
    }

    @Override
    public void handle(Page page, TemplateContext local, TemplateContext global) {
        String slug = global.getRequestContext().length > 1 ? global.getRequestContext()[1] : null;
        Category category = getCategory() != null ? getCategory() : page.getSite().categoryForSlug(slug);
        local.put("category", category.makeWrap());
        global.put("category", category.makeWrap());

        PostsPresentationBean postsPresentation = new PostsPresentationBean(category.getPostsSet());
        int currentPage = postsPresentation.currentPage(global.getParameter("p"));
        HashMap<String, Object> pagination = postsPresentation.paginate(page, currentPage, POSTS_PER_PAGE);
        List<Wrap> posts = postsPresentation.getVisiblePosts().stream().collect(Collectors.toList());
        local.put("posts", posts);
        local.put("pagination", pagination);

        global.put("posts", posts);
        global.put("pagination", pagination);
    }

    @Override
    public Component clone(CloneCache cloneCache) {
        return cloneCache.getOrClone(this, obj -> {
            ListCategoryPosts clone = new ListCategoryPosts((Category) null);
            cloneCache.setClone(ListCategoryPosts.this, clone);
            clone.setCategory(getCategory().clone(cloneCache));
            return clone;
        });
    }

    @Override
    @Atomic(mode = TxMode.WRITE)
    public void delete() {
        this.setCategory(null);
        super.delete();
    }

    @Override
    public JsonObject json() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", getType());
        jsonObject.addProperty("category", getCategory().getSlug());
        jsonObject.addProperty("site", getCategory().getSite().getSlug());
        jsonObject.addProperty("page", Optional.ofNullable(getPage()).map(Page::getSlug).orElse(null));
        return jsonObject;
    }

    @Override
    public String getName() {
        String name = super.getName();
        if (getCategory() != null) {
            return name + " (" + getCategory().getName().getContent() + ")";
        } else {
            return name;
        }
    }

    public Page getPage() {
        if (getInstalledPageSet().isEmpty()) {
            return null;
        } else {
            return getInstalledPageSet().iterator().next();
        }
    }

    public static class CategoriesForSite implements ComponentContextProvider<Category> {
        @Override
        public Collection<Category> provide(Page page) {
            return page.getSite().getCategoriesSet();
        }

        @Override
        public String present(Category category) {
            return category.getName().getContent();
        }
    }

}
