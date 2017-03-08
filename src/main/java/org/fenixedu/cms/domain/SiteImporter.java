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
package org.fenixedu.cms.domain;

import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.io.servlet.FileDownloadServlet;
import org.fenixedu.cms.domain.component.CMSComponent;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.component.ComponentDescriptor;
import org.fenixedu.cms.domain.component.ListCategoryPosts;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import pt.ist.fenixframework.Atomic;

import java.io.*;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.util.Optional.ofNullable;
import static org.apache.tika.io.FilenameUtils.normalize;
import static org.fenixedu.commons.i18n.LocalizedString.fromJson;

/**
 * Created by borgez-dsi on 24-06-2015.
 */
public class SiteImporter {
    private static final JsonParser PARSER = new JsonParser();
    private final ZipFile zipFile;

    public SiteImporter(ZipFile zipFile) {
        this.zipFile = zipFile;
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public Site importSite() {
        JsonObject siteJson = getSiteJson();
        Map<String, JsonObject> pagesDir = readJsonDirectory("pages/");
        Map<String, JsonObject> postsDir = readJsonDirectory("posts/");
        Map<String, JsonObject> categoriesDir = readJsonDirectory("categories/");
        Map<String, JsonObject> menusDir = readJsonDirectory("menus/");
        Site site = new Site(fromJson(siteJson.get("name")), fromJson(siteJson.get("description")));
        site.setSlug(siteJson.get("slug").getAsString());
        site.setCanViewGroup(Group.parse(siteJson.get("canViewGroup").getAsString()));

        if (siteJson.has("themeType") && !siteJson.get("themeType").isJsonNull()) {
            site.setThemeType(siteJson.get("themeType").getAsString());
        }
        site.setEmbedded(siteJson.get("embedded").getAsBoolean());
        if (siteJson.has("analyticsCode") && !siteJson.get("analyticsCode").isJsonNull()) {
            site.setAnalyticsCode(siteJson.get("analyticsCode").getAsString());
        }

        for (Map.Entry<String, JsonObject> entry : categoriesDir.entrySet()) {
            importCategory(site, entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, JsonObject> entry : postsDir.entrySet()) {
            importPost(site, entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, JsonObject> entry : pagesDir.entrySet()) {
            importPage(site, entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, JsonObject> entry : menusDir.entrySet()) {
            importMenu(site, entry.getKey(), entry.getValue());
        }


        if (siteJson.has("initialPage") && !siteJson.get("initialPage").isJsonNull()) {
            site.setInitialPage(site.pageForSlug(siteJson.get("initialPage").getAsString()));
        }

        site.updateMenuFunctionality();

        return site;
    }

    private Page importPage(Site site, String pageSlug, JsonObject jsonObject) {
    
        try{
            return site.pageForSlug(pageSlug);
        } catch(CmsDomainException e){
    
            Page page = new Page(site, fromJson(jsonObject.get("name")));
            page.setSlug(jsonObject.get("slug").getAsString());
            page.setCanViewGroup(Group.parse(jsonObject.get("canViewGroup").getAsString()));
            if (jsonObject.has("templateType") && !jsonObject.get("templateType").isJsonNull()) {
                page.setTemplateType(jsonObject.get("templateType").getAsString());
            }
            
            page.setPublished(jsonObject.get("published").getAsBoolean());
            for (JsonElement el : jsonObject.get("components").getAsJsonArray()) {
                Component component = importComponent(el.getAsJsonObject());
                if (component != null) {
                    page.addComponents(component);
                }
            }
            
            return page;
        }
    }

    private Component importComponent(JsonObject jsonObject) {
        Component component = null;
        try {
            ComponentDescriptor descriptor = Component.forType(jsonObject.get("type").getAsString());
            if (descriptor != null && !descriptor.isStateless()) {
                component = descriptor.fromJson(jsonObject);
            } else {
                component = Component.forType((Class<? extends CMSComponent>) descriptor.getType());
            }
        } catch (Exception e) {
        }
        return component;
    }

    private Menu importMenu(Site site, String menuSlug, JsonObject jsonObject) {
        return ofNullable(site.menuForSlug(menuSlug)).orElseGet(() -> {
            Menu menu = new Menu(site, fromJson(jsonObject.get("name")));
            menu.setSlug(jsonObject.get("slug").getAsString());
            for (JsonElement childrenEl : jsonObject.get("topLevelItems").getAsJsonArray()) {
                importMenuItem(site, menu, null, childrenEl.getAsJsonObject());
            }
            return menu;
        });
    }

    private MenuItem importMenuItem(Site site, Menu menu, MenuItem parent, JsonObject jsonObject) {
        MenuItem menuItem = new MenuItem(menu);
        menuItem.setName(fromJson(jsonObject.get("name")));
        menuItem.setPosition(jsonObject.get("position").getAsInt());
        menuItem.setFolder(jsonObject.get("isFolder").getAsBoolean());
        if (jsonObject.has("top") && !jsonObject.get("top").isJsonNull() && menu != null) {
            menuItem.setTop(menu);
        }
        if (jsonObject.has("parent") && !jsonObject.get("parent").isJsonNull() && parent != null) {
            menuItem.setParent(parent);
        }
        if (jsonObject.has("page")) {
            menuItem.setPage(site.pageForSlug(jsonObject.get("page").getAsString()));
        }
        for (JsonElement childrenEl : jsonObject.get("children").getAsJsonArray()) {
            importMenuItem(site, menu, menuItem, childrenEl.getAsJsonObject());
        }
        return menuItem;
    }

    private Post importPost(Site site, String postSlug, JsonObject jsonObject) {
        return ofNullable(site.postForSlug(postSlug)).orElseGet(() -> {

            Post post = new Post(site);
            LocalizedString body = fromJson(jsonObject.get("body"));
            LocalizedString excerpt = jsonObject.has("excerpt") ? fromJson(jsonObject.get("excerpt")) : null;

            post.setName(fromJson(jsonObject.get("name")));
            post.setSlug(jsonObject.get("slug").getAsString());
            post.setCanViewGroup(Group.parse(jsonObject.get("canViewGroup").getAsString()));
            post.setActive(jsonObject.get("active").getAsBoolean());
            if (jsonObject.has("location") && jsonObject.get("location").isJsonObject()) {
                post.setLocation(fromJson(jsonObject.get("location")));
            }
            if (jsonObject.has("metadata") && jsonObject.get("metadata").isJsonObject()) {
                post.setMetadata(PostMetadata.fromJson(jsonObject.get("metadata")));
            }
            if (jsonObject.has("publicationBegin") && jsonObject.get("publicationBegin").isJsonPrimitive()) {
                post.setPublicationBegin(DateTime.parse(jsonObject.get("publicationBegin").getAsString()));
            }
            if (jsonObject.has("publicationEnd") && jsonObject.get("publicationEnd").isJsonPrimitive()) {
                post.setPublicationEnd(DateTime.parse(jsonObject.get("publicationEnd").getAsString()));
            }

            if (jsonObject.has("categories") && jsonObject.get("categories").isJsonArray()) {
                for (JsonElement catSlug : jsonObject.get("categories").getAsJsonArray()) {
                    post.addCategories(site.categoryForSlug(catSlug.getAsString()));
                }
            }

            for (JsonElement postFileEl : jsonObject.get("files").getAsJsonArray()) {
                JsonObject postFileJson = postFileEl.getAsJsonObject();
             
                try {
                    InputStream inputStream = getZipFile().getInputStream(
                            getZipFile().getEntry(normalize("files/" + postFileJson.get("file").getAsString())));
                    GroupBasedFile file = new GroupBasedFile(postFileJson.get("displayName").getAsString(),
                            postFileJson.get("fileName").getAsString(),inputStream,
                            Group.parse(postFileJson.get("viewGroup").getAsString()));
                    new PostFile(post, file, postFileJson.get("isEmbedded").getAsBoolean(), postFileJson.get("index").getAsInt());
                    body = replace(body, postFileJson.get("url").getAsString(), FileDownloadServlet.getDownloadUrl(file));
                    excerpt = replace(excerpt, postFileJson.get("url").getAsString(), FileDownloadServlet.getDownloadUrl(file));
                } catch (IOException e) {
                    throw new RuntimeException("Failed to import file "+ postFileJson.get("url").getAsString());
                }
            }

            post.setBodyAndExcerpt(body, excerpt);

            return post;
        });
    }

    private LocalizedString replace(LocalizedString localized, String origContent, String newContent) {
        LocalizedString result = new LocalizedString();
        for (Locale locale : localized.getLocales()) {
            result = result.with(locale, localized.getContent(locale).replace(origContent, newContent));
        }
        return result;
    }

   
    private Category importCategory(Site site, String categorySlug, JsonObject jsonObject) {
        Category category = site.getOrCreateCategoryForSlug(categorySlug, fromJson(jsonObject.get("name")));
        for (JsonElement el : jsonObject.get("components").getAsJsonArray()) {
            Component component = importComponent(el.getAsJsonObject());
            if (component != null && ListCategoryPosts.class.isInstance(component)) {
                category.addComponents((ListCategoryPosts) component);
            }
        }
        System.out.println("importCategory - result: " + category);
        return category;
    }

    private JsonObject getSiteJson() {
        return getZipEntries("site.json");
    }

    private JsonObject getZipEntries(String fileName) {
        try {
            Reader reader = new InputStreamReader(getZipFile().getInputStream(getZipFile().getEntry(fileName)));
            JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();
            reader.close();
            return jsonObject;
        } catch (IOException e) {
            return new JsonObject();
        }
    }

    private Map<String, JsonObject> readJsonDirectory(String dirName) {
        Map<String, JsonObject> directoryEntries = Maps.newHashMap();
        Enumeration<? extends ZipEntry> zipEntries = getZipFile().entries();
        while (zipEntries.hasMoreElements()) {
            ZipEntry entry = zipEntries.nextElement();
            if (!entry.isDirectory() && entry.getName().startsWith(dirName) && entry.getName().endsWith(".json")) {
                try {
                    JsonObject json = PARSER.parse(new InputStreamReader(getZipFile().getInputStream(entry))).getAsJsonObject();
                    String entryName = entry.getName().substring(dirName.length(), entry.getName().lastIndexOf(".json"));
                    directoryEntries.put(entryName, json);
                } catch (IOException e) {
                }
            }
        }
        return directoryEntries;
    }

    public ZipFile getZipFile() {
        return zipFile;
    }
}
