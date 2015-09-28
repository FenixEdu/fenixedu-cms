package org.fenixedu.cms.ui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.fenixedu.bennu.core.api.UserResource;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.io.servlet.FileDownloadServlet;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.PermissionEvaluation;
import org.fenixedu.cms.domain.PermissionsArray.Permission;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.PostFile;
import org.fenixedu.cms.domain.PostMetadata;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import static java.util.Optional.ofNullable;
import static org.fenixedu.cms.domain.PermissionEvaluation.canDoThis;

/**
 * Created by borgez on 30-07-2015.
 */
@Service
public class AdminPostsService {

    @Atomic(mode = Atomic.TxMode.WRITE)
    public Post createPost(Site site, LocalizedString name) {
	Post post = new Post(site);
	post.setName(Post.sanitize(name));
	post.setBody(new LocalizedString());
	post.setCanViewGroup(site.getCanViewGroup());
	post.setActive(false);
	return post;
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public PostFile createFile(Post post, String name, Boolean embedded, Group canViewGroup, MultipartFile file) {
	try {
	    AdminSites.canEdit(post.getSite());
	    GroupBasedFile groupBasedFile = new GroupBasedFile(name, name, file.getBytes(), canViewGroup);
	    return new PostFile(post, groupBasedFile, embedded, post.getFilesSet().size());
	} catch(IOException e) {
	    throw new RuntimeException("Error creating Post File for post " + post, e);
	}
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void processPostChanges(Site site, Post post, JsonObject postJson) {
	AdminSites.canEdit(site);

	LocalizedString name = Post.sanitize(LocalizedString.fromJson(postJson.get("name")));
	LocalizedString body = Post.sanitize(LocalizedString.fromJson(postJson.get("body")));
      	String slug = ofNullable(postJson.get("slug"))
	    .map(JsonElement::getAsString).orElse(post.getSlug());

	if(!post.getName().equals(name)) {
	    post.setName(name);
	}
	if(!post.getBody().equals(body)) {
	    post.setBody(body);
	}
	if(!post.getSlug().equals(slug)) {
	    post.setSlug(slug);
	}

	processCategoryChanges(site, post, postJson);
	processFileChanges(site, post, postJson);
	processPublicationChanges(site, post, postJson);
	post.fixOrder(post.getFilesSorted());
    }



    private boolean equalDates(DateTime time1,DateTime time2) {
	return ofNullable(time1).map(DateTime::toString).orElse("").equals(ofNullable(time2).map(DateTime::toString).orElse(""));
    }

    public JsonObject serializePost(Post post) {
	JsonObject postJson = new JsonObject();
	JsonArray categoriesJson = new JsonArray();
	JsonArray filesJson = new JsonArray();
      	if(canDoThis(post.getSite(), Permission.LIST_CATEGORIES, Permission.EDIT_CATEGORY)) {
	  post.getSite().getCategoriesSet().stream().sorted(Category.CATEGORY_NAME_COMPARATOR)
	      .map(category -> serializeCategory(category, post)).forEach(categoriesJson::add);
	}
	post.getFilesSorted().stream().map(this::serializePostFile).forEach(filesJson::add);
	postJson.addProperty("slug", post.getSlug());
	postJson.add("name", ofNullable(post.getName()).map(LocalizedString::json)
	    .orElseGet(JsonObject::new));
	postJson.add("body", ofNullable(post.getBody()).map(LocalizedString::json).orElseGet(JsonObject::new));
	postJson.add("metadata", ofNullable(post.getMetadata()).map(PostMetadata::json).orElseGet(JsonObject::new));
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
		    .map(JsonElement::getAsString)
		    .map(DateTime::parse)
		    .orElse(null);

	    DateTime publicationEnds = ofNullable(postJson.get("publicationEnd"))
		.filter(JsonElement::isJsonPrimitive)
		    .map(JsonElement::getAsString)
		    .map(DateTime::parse)
		    .orElse(null);

	    Group canViewGroup = ofNullable(postJson.get("canViewGroup"))
		.map(JsonElement::getAsString)
		    .map(Group::parse)
		    .orElse(post.getCanViewGroup());

	    User createdBy = ofNullable(postJson.get("createdBy"))
	      .map(JsonElement::getAsJsonObject)
	      .map(json -> json.get("username").getAsString())
		  .map(User::findByUsername)
		  .orElse(post.getCreatedBy());

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
	      if(!post.getCreatedBy().equals(createdBy)) {
		post.setCreatedBy(createdBy);
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
	      }
	      newCategories.add(category);
	    }
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
	  }
	}
      }
    }


    private boolean canPublish(Post post) {
      	return (post.isStaticPost() && canDoThis(post.getSite(), Permission.PUBLISH_PAGES)) ||
	       (!post.isStaticPost() && canDoThis(post.getSite(), Permission.PUBLISH_POSTS));
    }
}
