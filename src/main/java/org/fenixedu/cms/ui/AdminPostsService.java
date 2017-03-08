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
package org.fenixedu.cms.ui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.api.UserResource;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.signals.DomainObjectEvent;
import org.fenixedu.bennu.core.signals.Signal;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.io.servlet.FileDownloadServlet;
import org.fenixedu.cms.domain.*;
import org.fenixedu.cms.domain.PermissionsArray.Permission;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static org.fenixedu.cms.domain.PermissionEvaluation.canDoThis;
import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;

/**
 * Created by borgez on 30-07-2015.
 */
@Service
public class AdminPostsService {

    @Atomic(mode = Atomic.TxMode.WRITE)
    public Post createPost(Site site, LocalizedString name) {
	Post post = new Post(site);
	post.setName(Post.sanitize(name));
	post.setBodyAndExcerpt(new LocalizedString(), new LocalizedString() );
	post.setCanViewGroup(site.getCanViewGroup());
	post.setPublicationBegin(new DateTime());
	post.setActive(false);
	return post;
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public PostFile createFile(Post post, String name, Boolean embedded, Group canViewGroup, File file) throws IOException {

			GroupBasedFile groupBasedFile = new GroupBasedFile(name, name, file, canViewGroup);
			return new PostFile(post, groupBasedFile, embedded, post.getFilesSet().size());
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void processPostChanges(Site site, Post post, JsonObject postJson) {
		LocalizedString name = Post.sanitize(LocalizedString.fromJson(postJson.get("name")));
		LocalizedString body = Post.sanitize(LocalizedString.fromJson(postJson.get("body")));
		LocalizedString excerpt = Post.sanitize(LocalizedString.fromJson(postJson.get("excerpt")));

		String slug = ofNullable(postJson.get("slug"))
				.map(JsonElement::getAsString).orElse(post.getSlug());

		if(!post.getName().equals(name)) {
			post.setName(name);
		}
		if(!post.getBody().equals(body) || (  post.getExcerpt()==null && excerpt!=null ) || !post.getExcerpt().equals(excerpt)) {
			post.setBodyAndExcerpt(body, excerpt);
		}
		if(!post.getSlug().equals(slug)) {
			post.setSlug(slug);
		}

		processCategoryChanges(site, post, postJson);
		processFileChanges(site, post, postJson);
		processPublicationChanges(site, post, postJson);
		post.fixOrder(post.getFilesSorted());
		Signal.emit(Post.SIGNAL_EDITED,new DomainObjectEvent<>(post));
    }



    private boolean equalDates(DateTime time1,DateTime time2) {
	return ofNullable(time1).map(DateTime::toString).orElse("").equals(ofNullable(time2).map(DateTime::toString).orElse(""));
    }

    public JsonObject serializePost(Post post) {
	JsonObject postJson = new JsonObject();
	JsonArray categoriesJson = new JsonArray();
	JsonArray filesJson = new JsonArray();
      	if(canDoThis(post.getSite(), Permission.LIST_CATEGORIES, Permission.EDIT_CATEGORY)) {
	  boolean canUsePrivileged = canDoThis(post.getSite(), Permission.USE_PRIVILEGED_CATEGORY);
	  post.getSite().getCategoriesSet().stream()
	      .filter(cat -> canUsePrivileged || !cat.getPrivileged())
	      .sorted(Category.CATEGORY_NAME_COMPARATOR)
	      .map(category -> serializeCategory(category, post)).forEach(categoriesJson::add);
	}
	post.getFilesSorted().stream().map(this::serializePostFile).forEach(filesJson::add);
	postJson.addProperty("slug", post.getSlug());
	postJson.add("name", ofNullable(post.getName()).map(LocalizedString::json)
	    .orElseGet(JsonObject::new));
	postJson.add("body", ofNullable(post.getBody()).map(LocalizedString::json).orElseGet(JsonObject::new));
	postJson.add("excerpt", ofNullable(post.getExcerpt()).map(LocalizedString::json).orElseGet(JsonObject::new));

      	if(PermissionEvaluation.canDoThis(post.getSite(), Permission.SEE_METADATA)) {
	  postJson.add("metadata", ofNullable(post.getMetadata()).map(PostMetadata::json)
	      .orElseGet(JsonObject::new));
	} else {
	  postJson.add("metadata", JsonNull.INSTANCE);
	}
	postJson.add("categories", categoriesJson);
	postJson.add("files", filesJson);
	postJson.addProperty("address", post.getAddress());

	if(canPublish(post)) {
	  postJson.addProperty("active", post.getActive());
	  postJson.addProperty("canViewGroup", post.getCanViewGroup().getExpression());
	  postJson.add("createdBy", UserResource.getBuilder().view(post.getCreatedBy()));
	  postJson.addProperty("publicationBegin", ofNullable(post.getPublicationBegin())
	      .map(DateTime::toString).orElse(null));
	  postJson.addProperty("publicationEnd", ofNullable(post.getPublicationEnd())
	      .map(DateTime::toString).orElse(null));
	}

      return postJson;
    }

    private JsonObject serializeCategory(Category category, Post post) {
	JsonObject categoryJson = new JsonObject();
	categoryJson.add("name", category.getName().json());
	categoryJson.addProperty("slug", category.getSlug());
	categoryJson.addProperty("use", post.getCategoriesSet().contains(category));
	return categoryJson;
    }

    public JsonObject serializePostFile(PostFile postFile) {
	JsonObject fileJson = new JsonObject();
	fileJson.addProperty("id", postFile.getExternalId());
	fileJson.addProperty("displayName", postFile.getFiles().getDisplayName());
	fileJson.addProperty("fileName", postFile.getFiles().getFilename());
	fileJson.addProperty("contentType", postFile.getFiles().getContentType());
	fileJson.addProperty("editUrl", postFile.getEditUrl());
	fileJson.addProperty("isEmbedded", postFile.getIsEmbedded());
	fileJson.addProperty("index", postFile.getIndex());
	fileJson.addProperty("canViewGroup", postFile.getFiles().getAccessGroup().getExpression());
	fileJson.addProperty("url", FileDownloadServlet.getDownloadUrl(postFile.getFiles()));
	return fileJson;
    }

    private void processPublicationChanges(Site site, Post post, JsonObject postJson) {
	if(canPublish(post)) {

	    boolean active = ofNullable(postJson.get("active"))
		  .map(JsonElement::getAsBoolean)
		  .orElse(false);

	      DateTime publicationBegin = ofNullable(postJson.get("publicationBegin"))
		  .filter(JsonElement::isJsonPrimitive)
		    .map(JsonElement::getAsString).filter(x -> !x.isEmpty())
		    .map(DateTime::parse)
		    .orElse(null);

	    DateTime publicationEnds = ofNullable(postJson.get("publicationEnd"))
		.filter(JsonElement::isJsonPrimitive)
		    .map(JsonElement::getAsString).filter(x -> !x.isEmpty())
		    .map(DateTime::parse)
		    .orElse(null);

	    Group canViewGroup = ofNullable(postJson.get("canViewGroup"))
		.map(JsonElement::getAsString)
		    .map(Group::parse)
		    .orElse(post.getCanViewGroup());

	      if(PermissionEvaluation.canDoThis(site, Permission.CHANGE_OWNERSHIP_POST)) {
		User createdBy = ofNullable(postJson.get("createdBy"))
		    .map(JsonElement::getAsJsonObject)
		    .map(json -> json.get("username").getAsString())
		    .map(User::findByUsername)
		    .orElse(post.getCreatedBy());
		if(!post.getCreatedBy().equals(createdBy)) {
		  post.setCreatedBy(createdBy);
		}
	      }

	      if(!equalDates(post.getPublicationBegin(), publicationBegin)) {
		post.setPublicationBegin(publicationBegin);
	      }
	      if(!equalDates(post.getPublicationEnd(), publicationEnds)) {
		post.setPublicationEnd(publicationEnds);
	      }
	      if(!post.getCanViewGroup().equals(canViewGroup)) {
		post.setCanViewGroup(canViewGroup);
	      }
	      if(post.getActive() != active) {
		post.setActive(active);
	      }

      	}
    }

    private void processCategoryChanges(Site site, Post post, JsonObject postJson) {
      if(canDoThis(post.getSite(), Permission.LIST_CATEGORIES, Permission.EDIT_CATEGORY)) {
	if (postJson.get("categories") != null && postJson.get("categories").isJsonArray()) {
	  Set<Category> newCategories = new HashSet<>();
	  for (JsonElement categoryJsonEl : postJson.get("categories").getAsJsonArray()) {
	    JsonObject categoryJson = categoryJsonEl.getAsJsonObject();
	    if (ofNullable(categoryJson.get("use")).map(JsonElement::getAsBoolean).orElse(false)) {
	      String categorySlug = categoryJson.get("slug").getAsString();
	      LocalizedString
		  categoryName =
		  Post.sanitize(LocalizedString.fromJson(categoryJson.get("name")));
	      Category category = site.categoryForSlug(categorySlug);
	      if (category == null) {
		PermissionEvaluation
		    .ensureCanDoThis(site, Permission.CREATE_CATEGORY);
		category = new Category(site, categoryName);
	      } else if(category.getPrivileged()) {
		ensureCanDoThis(site, Permission.USE_PRIVILEGED_CATEGORY);
	      }
	      newCategories.add(category);
	    }
	  }

	  if(!canDoThis(site, Permission.USE_PRIVILEGED_CATEGORY)) {
	    //If the user has no access to remove previleged categories, then we add them back
	    HashSet<Category> removed = new HashSet<>(post.getCategoriesSet());
	    removed.removeAll(newCategories);
	    removed.stream().filter(Category::getPrivileged).forEach(newCategories::add);
	  }

	  if (!newCategories.containsAll(post.getCategoriesSet()) || !post.getCategoriesSet()
	      .containsAll(newCategories)) {
	    post.getCategoriesSet().clear();
	    newCategories.stream().forEach(post::addCategories);
	  }
	}
      }
    }

	private void processFileChanges(Site site, Post post, JsonObject postJson) {
		if(postJson.get("files")!=null && postJson.get("files").isJsonArray()) {
			for (JsonElement fileJsonEl : postJson.get("files").getAsJsonArray()) {
				JsonObject fileJson = fileJsonEl.getAsJsonObject();
				PostFile postFile = FenixFramework.getDomainObject(fileJson.get("id").getAsString());
				if(postFile.getPost() == post) {
					int index = fileJson.get("index").getAsInt();
					boolean isEmbedded = fileJson.get("isEmbedded").getAsBoolean();
					if(postFile.getIndex()!= index) {
						postFile.setIndex(index);
					}
					if(postFile.getIsEmbedded()!=isEmbedded) {
						postFile.setIsEmbedded(isEmbedded);
					}
					Signal.emit(PostFile.SIGNAL_EDITED, new DomainObjectEvent<>(postFile));
				}
			}
		}

	}


    private boolean canPublish(Post post) {
      	return (post.isStaticPost() && canDoThis(post.getSite(), Permission.PUBLISH_PAGES)) ||
	       (!post.isStaticPost() && canDoThis(post.getSite(), Permission.PUBLISH_POSTS));
    }
}
