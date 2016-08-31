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

import static java.nio.charset.Charset.defaultCharset;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.tika.io.FilenameUtils;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.io.servlets.FileDownloadServlet;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Created by borgez-dsi on 24-06-2015.
 */
public class SiteExporter {

    private final Site site;

    public SiteExporter(Site site) {
        this.site = site;
    }

    public ByteArrayOutputStream export() {
        try {
            ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(byteArrayStream));

            addToZipFile("site.json", export(site), zipOutputStream);

            for (Page page : getSite().getPagesSet()) {
                addToZipFile("pages/" + page.getSlug() + ".json", export(page), zipOutputStream);
            }
            for (Post post : getSite().getPostSet()) {
                addToZipFile("posts/" + post.getSlug() + ".json", export(post), zipOutputStream);
            }
            for (Category category : getSite().getCategoriesSet()) {
                addToZipFile("categories/" + category.getSlug() + ".json", export(category), zipOutputStream);
            }
            for (Menu menu : getSite().getMenusSet()) {
                addToZipFile("menus/" + menu.getSlug() + ".json", export(menu), zipOutputStream);
            }
            for (GroupBasedFile file : getSite().getPostSet().stream()
                    .flatMap(post -> post.getFilesSet().stream()).map(PostFile::getFiles).distinct().collect(toList())) {
                addToZipFile("files/" + file.getExternalId(), file.getStream(), zipOutputStream);
            }
            zipOutputStream.close();
            return byteArrayStream;
        } catch (IOException e) {
            throw new RuntimeException("Error exporting site " + site.getSlug(), e);
        }
    }

    public static void addToZipFile(String filename, JsonObject jsonObject, ZipOutputStream zos) throws IOException {
        addToZipFile(filename, new ByteArrayInputStream(jsonObject.toString().getBytes(defaultCharset())), zos);
    }

    public static void addToZipFile(String filename, InputStream content, ZipOutputStream zos) throws IOException {
        ZipEntry zipEntry = new ZipEntry(FilenameUtils.normalize(filename));
        zos.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = content.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }
        zos.closeEntry();
        content.close();
    }

    protected JsonObject export(Site site) {
        JsonObject siteJson = new JsonObject();
        siteJson.addProperty("slug", site.getSlug());
        siteJson.add("name", site.getName().json());
        siteJson.add("description", site.getDescription().json());
        siteJson.add("canViewGroup", export(site.getCanViewGroup()));
        siteJson.addProperty("themeType", site.getThemeType());
        siteJson.addProperty("embedded", site.getEmbedded());
        siteJson.addProperty("analyticsCode", site.getAnalyticsCode());
        siteJson.add("creationDate", toJson(site.getCreationDate()));
        siteJson.addProperty("createdBy", site.getCreatedBy().getUsername());
        siteJson.addProperty("folder", ofNullable(site.getFolder()).map(CMSFolder::getExternalId).orElse(null));
        siteJson.addProperty("published", site.getPublished());
        siteJson.addProperty("initialPage", ofNullable(site.getInitialPage()).map(Page::getSlug).orElse(null));
        siteJson.add("menus", toArray(site.getMenusSet().stream().map(Menu::getSlug)));
        siteJson.add("posts", toArray(site.getPostSet().stream().map(Post::getSlug)));
        siteJson.add("pages", toArray(site.getPagesSet().stream().map(Page::getSlug)));
        siteJson.add("categories", toArray(site.getCategoriesSet().stream().map(Category::getSlug)));
        return siteJson;
    }

    protected JsonObject export(Page page) {
        JsonObject pageJson = new JsonObject();
        pageJson.addProperty("slug", page.getSlug());
        pageJson.add("name", page.getName().json());
        pageJson.addProperty("site", page.getSite().getSlug());
        pageJson.add("canViewGroup", export(page.getCanViewGroup()));
        pageJson.addProperty("templateType", page.getTemplateType());
        pageJson.addProperty("createdBy", page.getCreatedBy().getUsername());
        pageJson.add("creationDate", toJson(page.getCreationDate()));
        pageJson.addProperty("published", page.getPublished());
        pageJson.add("modificationDate", toJson(page.getModificationDate()));
        pageJson.addProperty("published", page.getPublished());
        pageJson.add("menuItems", toArray(page.getMenuItemsSet().stream().map(MenuItem::getExternalId)));
        pageJson.add("components", toJsonArray(page.getComponentsSet().stream().map(this::export)));
        return pageJson;
    }

    protected JsonObject export(Menu menu) {
        JsonObject json = new JsonObject();
        json.addProperty("slug", menu.getSlug());
        json.addProperty("site", menu.getSite().getSlug());
        json.add("creationDate", toJson(menu.getCreationDate()));
        json.addProperty("createdBy", menu.getCreatedBy().getUsername());
        json.addProperty("order", menu.getOrder());
        json.add("name", menu.getName().json());
        json.add("topLevelItems", toJsonArray(menu.getToplevelItemsSet().stream().map(this::export)));
        return json;
    }

    protected JsonObject export(MenuItem menuItem) {
        JsonObject json = new JsonObject();
        json.add("name", menuItem.getName().json());
        json.addProperty("slug", menuItem.getExternalId());
        json.addProperty("menu", ofNullable(menuItem.getMenu()).map(Menu::getSlug).orElse(null));
        json.addProperty("top", ofNullable(menuItem.getTop()).map(Menu::getSlug).orElse(null));
        json.addProperty("parent", ofNullable(menuItem.getParent()).map(MenuItem::getExternalId).orElse(null));
        json.addProperty("position", ofNullable(menuItem.getPosition()).orElse(0));
        json.addProperty("isFolder", menuItem.getFolder());
        json.addProperty("page", ofNullable(menuItem.getPage()).map(Page::getSlug).orElse(null));
        json.addProperty("createdBy", menuItem.getCreatedBy().getUsername());
        json.add("creationDate", toJson(menuItem.getCreationDate()));
        json.add("children", toJsonArray(menuItem.getChildrenSet().stream().map(this::export)));
        return json;
    }

    protected JsonObject export(Post post) {
        JsonObject json = new JsonObject();
        json.addProperty("slug", post.getSlug());
        json.addProperty("site", post.getSite().getSlug());
        json.add("name", post.getName().json());
        json.add("body", post.getBody().json());
        json.addProperty("createdBy", post.getCreatedBy().getUsername());
        json.add("creationDate", toJson(post.getCreationDate()));
        json.add("canViewGroup", export(post.getCanViewGroup()));
        json.add("categories", toArray(post.getCategoriesSet().stream().map(Category::getSlug)));
        json.addProperty("active", post.getActive());
        json.add("location", ofNullable(post.getLocation()).map(LocalizedString::json).orElse(JsonNull.INSTANCE));
        json.add("metadata", ofNullable(post.getMetadata()).map(PostMetadata::json).orElse(JsonNull.INSTANCE));
        json.add("modificationDate", toJson(post.getModificationDate()));
        json.add("publicationBegin", toJson(post.getPublicationBegin()));
        json.add("publicationEnd", toJson(post.getPublicationEnd()));
        json.add("files", toJsonArray(post.getFilesSet().stream().map(this::export)));
        return json;
    }

    protected JsonObject export(Category category) {
        JsonObject json = new JsonObject();
        json.addProperty("slug", category.getSlug());
        json.addProperty("site", category.getSite().getSlug());
        json.add("creationDate", toJson(category.getCreationDate()));
        json.addProperty("createdBy", category.getCreatedBy().getUsername());
        json.add("name", category.getName().json());
        json.add("posts", toArray(category.getPostsSet().stream().map(Post::getSlug)));
        json.add("components", toJsonArray(category.getComponentsSet().stream().map(this::export)));
        return json;
    }

    protected JsonObject export(PostFile postFile) {
        JsonObject json = new JsonObject();
        json.addProperty("slug", postFile.getExternalId());
        json.addProperty("index", postFile.getIndex());
        json.addProperty("isEmbedded", postFile.getIsEmbedded());
        json.addProperty("post", postFile.getPost().getSlug());
        json.add("viewGroup", export(postFile.getFiles().getAccessGroup()));
        json.addProperty("displayName", postFile.getFiles().getDisplayName());
        json.addProperty("fileName", postFile.getFiles().getFilename());
        json.addProperty("url", FileDownloadServlet.getDownloadUrl(postFile.getFiles()));
        json.addProperty("file", postFile.getFiles().getExternalId());
        return json;
    }

    protected JsonObject export(Component component) {
        return component.json();
    }

    private JsonElement export(Group group) {
        return group != null ? new JsonPrimitive(group.getExpression()) : JsonNull.INSTANCE;
    }

    private JsonElement toJson(DateTime date) {
        return date != null ? new JsonPrimitive(date.toDateTimeISO().toString()) : JsonNull.INSTANCE;
    }

    private JsonArray toArray(Stream<String> strStream) {
        return toJsonArray(strStream.map(JsonPrimitive::new));
    }

    private JsonArray toJsonArray(Stream<JsonElement> jsonElementStream) {
        JsonArray jsonArray = new JsonArray();
        jsonElementStream.forEach(jsonArray::add);
        return jsonArray;
    }

    public Site getSite() {
        return site;
    }
}
