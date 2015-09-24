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

import com.google.common.collect.ImmutableList;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.signals.DomainObjectEvent;
import org.fenixedu.bennu.signals.Signal;
import org.fenixedu.cms.domain.PermissionsArray.Permission;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.component.StaticPost;
import org.fenixedu.cms.domain.wraps.UserWrap;
import org.fenixedu.cms.domain.wraps.Wrap;
import org.fenixedu.cms.domain.wraps.Wrappable;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pt.ist.fenixframework.Atomic;

import static org.fenixedu.commons.i18n.LocalizedString.fromJson;

/**
 * A post models a given content to be presented to the user.
 */
public class Post extends Post_Base implements Wrappable, Sluggable, Cloneable {

    public static final String SIGNAL_CREATED = "fenixedu.cms.post.created";

    public static final Comparator<Post> CREATION_DATE_COMPARATOR = Comparator.comparing(Post::getCreationDate).reversed();

    /**
     * The logged {@link User} creates a new Post.
     */
    public Post(Site site) {
        super();
        if (Authenticate.getUser() == null) {
            throw CmsDomainException.forbiden();
        }
        this.setCreatedBy(Authenticate.getUser());
        DateTime now = new DateTime();
        this.setCreationDate(now);
        this.setModificationDate(now);
        this.setActive(false);
        this.setCanViewGroup(Group.anyone());
        this.setSite(site);

        Signal.emit(Post.SIGNAL_CREATED, new DomainObjectEvent<Post>(this));
    }

    @Override
    public Site getSite() {
        return super.getSite();
    }

    /**
     * saves the name of the post and creates a new slug for the post.
     */
    @Override
    public void setName(LocalizedString name) {
        LocalizedString prevName = getName();
        super.setName(name);
        this.setModificationDate(new DateTime());
        if (prevName == null) {
            String slug = StringNormalizer.slugify(name.getContent());
            setSlug(slug);
        }
    }

    @Override
    public void setSlug(String slug) {
        super.setSlug(SlugUtils.makeSlug(this, slug));
    }

    /**
     * A slug is valid if there are no other page on that site that have the
     * same slug.
     *
     * @param slug
     * @return true if it is a valid slug.
     */
    @Override
    public boolean isValidSlug(String slug) {
        Post p = getSite().postForSlug(slug);
        return p == null || p == this;
    }

    /**
     * @return the URL link to the slug's page.
     */
    public String getAddress() {
        if(isStaticPost()) {
            return getStaticPage().get().getAddress();
        } else {
            return Optional.ofNullable(getSite().getViewPostPage()).map(page->page.getAddress() + "/" + getSlug()).orElse(null);
        }
    }

    @Atomic
    public void delete() {
        setCreatedBy(null);
        setSite(null);
        setViewGroup(null);
        deleteFiles();
        setLatestRevision(null);

        getComponentSet().stream().forEach(Component::delete);
        getCategoriesSet().stream().forEach(category->category.removePosts(this));
        getRevisionsSet().stream().forEach(PostContentRevision::delete);

        this.deleteDomainObject();
    }

    public boolean hasPublicationPeriod() {
        return getPublicationBegin() != null && getPublicationEnd() != null;
    }

    public boolean isInPublicationPeriod() {
        boolean inBegin = getPublicationBegin() == null || getPublicationBegin().isBeforeNow();
        boolean inEnd = getPublicationEnd() == null || getPublicationEnd().isAfterNow();
        return inBegin && inEnd;
    }

    public boolean isVisible() {
        return getActive() && (!hasPublicationPeriod() || isInPublicationPeriod());
    }

    /**
     * returns the group of people who can view this site.
     *
     * @return group the access group for this site
     */
    public Group getCanViewGroup() {
        return getViewGroup().toGroup();
    }

    /**
     * sets the access group for this site
     *
     * @param group the group of people who can view this site
     */
    @Atomic
    public void setCanViewGroup(Group group) {
        super.setViewGroup(group.toPersistentGroup());
        Set<User> groupMembers = group.getMembers().collect(Collectors.toSet());
        getEmbeddedFilesSorted().forEach(postFile -> postFile.getFiles().setAccessGroup(group));
        //if the new group is more restricted then the attachment group is updated
        getAttachmentFilesSorted().map(PostFile::getFiles)
                        .filter(file -> !file.getAccessGroup().getMembers().allMatch(groupMembers::contains))
                        .forEach(file -> file.setAccessGroup(group));
    }

    public static Post create(Site site, Page page, LocalizedString name, LocalizedString body, Category category,
            boolean active, User creator) {
        Post post = new Post(site);
        post.setName(name);
        post.setBody(body);
        post.setCreationDate(new DateTime());
        if (creator == null) {
            post.setCreatedBy(page.getCreatedBy());
        } else {
            post.setCreatedBy(creator);
        }
        post.addCategories(category);
        post.setActive(active);
        return post;

    }

    public String getEditUrl() {
        return getStaticPage().map(Page::getEditUrl).orElse(CoreConfiguration.getConfiguration().applicationUrl() + "/cms/posts/" + getSite().getSlug() + "/" + getSlug() + "/edit");
    }

    public void fixOrder(List<PostFile> sortedItems) {
        for (int i = 0; i < sortedItems.size(); ++i) {
            sortedItems.get(i).setIndex(i);
        }
    }

    @Override
    public Post clone(CloneCache cloneCache) {
        return cloneCache.getOrClone(this, obj -> {
            Collection<Category> categories = new HashSet<>(getCategoriesSet());
            List<PostFile> files = new ArrayList<>(getFilesSorted());
            Post clone = new Post(getSite());
            cloneCache.setClone(Post.this, clone);
            clone.setBody(getBody() != null ? fromJson(getBody().json()) : null);
            clone.setName(getName() != null ? fromJson(getName().json()) : null);
            clone.setBody(getBody() != null ? fromJson(getBody().json()) : null);
            clone.setLocation(getLocation() != null ? fromJson(getLocation().json()) : null);
            clone.setMetadata(getMetadata() != null ? getMetadata().clone() : null);
            clone.setCanViewGroup(getCanViewGroup());
            clone.setPublicationBegin(getPublicationBegin());
            clone.setPublicationEnd(getPublicationEnd());
            clone.setCreatedBy(getCreatedBy());
            clone.setCreationDate(getCreationDate());
            clone.setViewGroup(getViewGroup());
            categories.stream().map(category -> category.clone(cloneCache)).forEach(clone::addCategories);
            files.stream().map(file -> file.clone(cloneCache)).forEach(clone::addFiles);
            return clone;
        });
    }

    public Stream<PostFile> getAttachmentFilesSorted() {
        return getFilesSet().stream().filter(pf -> !pf.getIsEmbedded()).sorted();
    }

    public Stream<PostFile> getEmbeddedFilesSorted() {
        return getFilesSet().stream().filter(pf -> pf.getIsEmbedded()).sorted();
    }

    private void deleteFiles() {
        ImmutableList.copyOf(getFilesSet()).forEach(PostFile::delete);
    }

    public List<PostFile> getFilesSorted() {
        return getFilesSet().stream().sorted().collect(Collectors.toList());
    }

    @Override
    public void addFiles(PostFile postFile) {
        List<PostFile> list = getFilesSorted();
        list.add(postFile.getIndex(), postFile);
        fixOrder(list);
        super.addFiles(postFile);
    }

    @Override
    public void removeFiles(PostFile files) {
        List<PostFile> list = getFilesSorted();
        list.remove(files);
        fixOrder(list);
        super.removeFiles(files);
    }

    @Override
    public void addCategories(Category categories) {
        DateTime now = new DateTime();
        super.addCategories(categories);
        categories.getComponentsSet().forEach(component -> {
            if (component.getPage() != null) {
                component.getPage().setModificationDate(now);
            }
        });
        setModificationDate(now);
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        setModificationDate(new DateTime());
    }

    public void setBody(LocalizedString body) {
        PostContentRevision pcr = new PostContentRevision();
        pcr.setBody(body);
        pcr.setCreatedBy(Authenticate.getUser());
        pcr.setPrevious(getLatestRevision());
        pcr.setPost(this);
        pcr.setRevisionDate(new DateTime());

        setLatestRevision(pcr);

        setModificationDate(new DateTime());
    }

    public LocalizedString getBody() {
        PostContentRevision pcr = getLatestRevision();
        if (pcr == null) {
            return new LocalizedString();
        } else {
            return pcr.getBody();
        }
    }

    public LocalizedString getPresentationBody() {
        return getBody();
    }

    @Override
    public void setLocation(LocalizedString location) {
        super.setLocation(location);
        setModificationDate(new DateTime());
    }

    @Override
    public void setPublicationEnd(DateTime publicationEnd) {
        super.setPublicationEnd(publicationEnd);
        setModificationDate(new DateTime());
    }

    @Override
    public void setPublicationBegin(DateTime publicationBegin) {
        super.setPublicationBegin(publicationBegin);
        setModificationDate(new DateTime());
    }

    public String getCategories() {
        return this.getCategoriesSet().stream().map(x -> x.getName().getContent()).reduce((x, y) -> x + "," + y).orElse("");
    }

    public String getCategoriesString() {
        return this.getCategoriesSet().stream().map(x -> x.getName().getContent()).reduce((x, y) -> x + "," + y).orElse("");
    }

    public Optional<Page> getStaticPage() {
        return getComponentSet().stream().filter(component -> StaticPost.class.isInstance(component))
                        .map(component -> ((StaticPost) component).getPage()).findFirst();
    }

    public boolean isStaticPost() {
        return getComponentSet().stream().filter(component -> StaticPost.class.isInstance(component)).findAny().isPresent();
    }

    public class PostWrap extends Wrap {

        public LocalizedString getName() {
            return Post.this.getName();
        }

        public String getSlug() {
            return Post.this.getSlug();
        }

        public boolean isVisible() {
            return Post.this.getCanViewGroup().isMember(Authenticate.getUser());
        }

        public String getVisibilityGroup() {
            return Post.this.getCanViewGroup().toPersistentGroup().getPresentationName();
        }

        public LocalizedString getBody() {
            return Post.this.getBody();
        }

        public Wrap getCreatedBy() {
            return new UserWrap(Post.this.getCreatedBy());
        }

        public DateTime getCreationDate() {
            return Post.this.getCreationDate();
        }

        public DateTime getPublicationBegin() {
            return Post.this.getPublicationBegin();
        }

        public DateTime getPublicationEnd() {
            return Post.this.getPublicationEnd();
        }

        public DateTime getModificationDate() {
            return Post.this.getModificationDate();
        }

        public Wrap getSite() {
            return Post.this.getSite().makeWrap();
        }

        public String getAddress() {
            return Post.this.getAddress();
        }

        public String getEditAddress() {
            return CoreConfiguration.getConfiguration().applicationUrl() + "/cms/posts/" + Post.this.getSite().getSlug() + "/"
                    + Post.this.getSlug() + "/edit";
        }

        public List<Wrap> getCategories() {
            return Post.this.getCategoriesSet().stream().map(Wrap::make).collect(Collectors.toList());
        }

        public List<Wrap> getAttachements() {
            return Post.this.getAttachmentFilesSorted().map(PostFile::makeWrap).collect(Collectors.toList());
        }

    }

    @Override
    public Wrap makeWrap() {
        return new PostWrap();
    }

    public boolean isAccessible() {
        return getCanViewGroup().isMember(Authenticate.getUser());
    }

    public boolean isModified() {
        return !getCreationDate().toLocalDate().equals(getModificationDate().toLocalDate());
    }

    public static LocalizedString sanitize(LocalizedString original) {
        return Sanitization.sanitize(original);
    }

    private static final Object NONE = new Object();

    public Iterator<PostContentRevision> getRevisionsIterator() {
        return new Iterator<PostContentRevision>() {
            PostContentRevision t = (PostContentRevision) NONE;

            @Override
            public boolean hasNext() {
                return t.getNext() != null;
            }

            @Override
            public PostContentRevision next() {
                return t = (t == NONE) ? getLatestRevision() : t.getNext();
            }
        };
    }

    public Iterable<PostContentRevision> getRevisions() {
        return () -> getRevisionsIterator();
    }

    public boolean canDelete() {
        Set<Permission> required = new HashSet<>();
        required.add(Permission.DELETE_POSTS);
        if(!Authenticate.getUser().equals(getCreatedBy())) {
            required.add(Permission.DELETE_POSTS);
        }
        if(isVisible()) {
            required.add(Permission.DELETE_POSTS_PUBLISHED);
        }
        return PermissionEvaluation.canDoThis(getSite(), required.toArray(new Permission[]{}));
    }
}
