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

import java.util.*;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.AnyoneGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.io.servlets.FileDownloadServlet;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.wraps.UserWrap;
import org.fenixedu.cms.domain.wraps.Wrap;
import org.fenixedu.cms.domain.wraps.Wrappable;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * A post models a given content to be presented to the user.
 */
public class Post extends Post_Base implements Wrappable {

    public static final Comparator<? super Post> CREATION_DATE_COMPARATOR = Comparator.comparing(Post::getCreationDate)
            .reversed();

    /**
     * The logged {@link User} creates a new Post.
     */
    public Post() {
        super();
        if (Authenticate.getUser() == null) {
            throw CmsDomainException.forbiden();
        }
        this.setCreatedBy(Authenticate.getUser());
        DateTime now = new DateTime();
        this.setCreationDate(now);
        this.setModificationDate(now);
        this.setActive(true);
        this.setCanViewGroup(AnyoneGroup.get());
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
            while (existsSlug(slug)) {
                slug = StringNormalizer.slugify(name.getContent() + UUID.randomUUID().toString().substring(0, 4));
            }
            setSlug(slug);
        }
    }

    /**
     * @return true if the site allready has a post registered with the slug received as argument and false otherwise.
     */
    private boolean existsSlug(String slug) {
        return getSite().getPostSet().stream().map(Post::getSlug).filter(postSlug -> slug.equals(postSlug)).findAny().isPresent();
    }

    /**
     * @return the URL link to the slug's page.
     */
    public String getAddress() {
        Page page = this.getSite().getViewPostPage();
        if (page == null && !this.getComponentSet().isEmpty()) {
            page = this.getComponentSet().iterator().next().getPage();
        }
        if (page != null) {
            return page.getAddress() + "/" + this.getSlug();
        }
        return null;
    }

    @Atomic
    public void delete() {
        for (Component c : this.getComponentSet()) {
            c.delete();
        }
        for (Category c : this.getCategoriesSet()) {
            removeCategories(c);
        }

        this.setCreatedBy(null);
        this.setSite(null);
        this.setViewGroup(null);
        this.getAttachments().delete();
        this.getPostFiles().delete();
        this.deleteDomainObject();

    }

    public void removeCategories() {
        DateTime now = new DateTime();
        new HashSet<>(getCategoriesSet()).forEach(c -> {
            removeCategories(c);
            c.getComponentsSet().forEach(component -> {
                if (component.getPage() != null) {
                    component.getPage().setModificationDate(now);
                }
            });
        });
        setModificationDate(now);
    }

    public boolean hasPublicationPeriod() {
        return getPublicationBegin() != null && getPublicationEnd() != null;
    }

    public boolean isInPublicationPeriod() {
        boolean inBegin = getPublicationBegin() == null || getPublicationBegin().isAfterNow();
        boolean inEnd = getPublicationEnd() == null || getPublicationEnd().isBeforeNow();
        return inBegin && inEnd;
    }

    public boolean isVisible() {
        return getActive() && (!hasPublicationPeriod() || isInPublicationPeriod());
    }

    /**
     * returns the group of people who can view this site.
     *
     * @return group
     *         the access group for this site
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
        setViewGroup(group.toPersistentGroup());

        for (GroupBasedFile file : getFilesSet()) {
            file.setAccessGroup(group);
        }
    }

    public static Post create(Site site, Page page, LocalizedString name, LocalizedString body, Category category,
            boolean active, User creator) {
        Post post = new Post();
        post.setSite(site);
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
        return this.getSite().getEditUrl() + "/" + this.getSlug() + "/edit";
    }

    private void fixOrder(List<PostFile> sortedItems) {
        for (int i = 0; i < sortedItems.size(); ++i) {
            sortedItems.get(i).setIndex(i);
        }
    }

    public class PostFiles {
        private PostFiles() {
        }

        public List<GroupBasedFile> getFiles() {
            return ImmutableList.copyOf(getFilesSet());
        }

        public void putFile(GroupBasedFile file) {
            Post.this.getFilesSet().add(file);
            file.setAccessGroup(Post.this.getCanViewGroup());
        }

        public void removeFile(GroupBasedFile file) {
            Post.this.getFilesSet().remove(file);
        }

        public boolean contains(GroupBasedFile file) {
            return Post.this.getFilesSet().contains(file);
        }

        public void delete() {
            Post.this.getFilesSet().forEach((a) -> {
                a.setPost(null);
                a.delete();
            });
        }
    }

    public class Attachments {

        private Attachments() {
        }

        public List<GroupBasedFile> getFiles() {
            return Post.this.getAttachementsSet().stream().sorted().map(x -> x.getFiles()).collect(Collectors.toList());
        }

        public void putFile(GroupBasedFile item, int position) {

            if (position < 0) {
                position = 0;
            } else if (position > Post.this.getAttachementsSet().size()) {
                position = Post.this.getAttachementsSet().size();
            }

            PostFile postFile = new PostFile();
            postFile.setIndex(position);
            postFile.setFiles(item);

            List<PostFile> list = Lists.newArrayList(Post.this.getAttachementsSet());
            list.add(position, postFile);

            fixOrder(list);

            Post.this.getAttachementsSet().add(postFile);
        }

        public GroupBasedFile removeFile(int position) {
            PostFile pf =
                    Post.this.getAttachementsSet().stream().filter(x -> x.getIndex() == position).findAny()
                            .orElseThrow(() -> new RuntimeException("Invalid Position"));
            GroupBasedFile f = pf.getFiles();

            pf.setFiles(null);
            pf.setPost(null);
            pf.delete();

            List<PostFile> list = Lists.newArrayList(Post.this.getAttachementsSet());

            fixOrder(list);

            return f;
        }

        public void move(int orig, int dest) {
            Set<PostFile> files = Post.this.getAttachementsSet();

            if (orig < 0 || orig >= files.size()) {
                throw new RuntimeException("Origin outside index bounds");
            }

            if (dest < 0 || dest >= files.size()) {
                throw new RuntimeException("Destiny outside index bounds");
            }

            putFile(removeFile(orig), dest);
        }

        public void delete() {
            Post.this.getAttachementsSet().forEach((a) -> {
                a.setPost(null);
                a.delete();
            });
        }
    }

    public Attachments getAttachments() {
        return new Attachments();
    }

    public PostFiles getPostFiles() {
        return new PostFiles();
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
    public void setActive(Boolean active) {
        super.setActive(active);
        setModificationDate(new DateTime());
    }

    @Override
    public void setBody(LocalizedString body) {
        super.setBody(body);
        setModificationDate(new DateTime());
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

    public class PostWrap extends Wrap {

        public LocalizedString getName() {
            return Post.this.getName();
        }

        public String getSlug() {
            return Post.this.getSlug();
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

        public List<ImmutableMap<String, Object>> getAttachments() {
            return Post.this
                    .getAttachments()
                    .getFiles()
                    .stream()
                    .map((f) -> ImmutableMap.of("name", (Object) f.getDisplayName(), "contentType", (Object) f.getContentType(),
                            "url", FileDownloadServlet.getDownloadUrl(f))).collect(Collectors.toList());
        }

        public List<ImmutableMap<String, Object>> getPostFiles() {
            return Post.this
                    .getPostFiles()
                    .getFiles()
                    .stream()
                    .map((f) -> ImmutableMap.of("name", (Object) f.getDisplayName(), "contentType", (Object) f.getContentType(),
                            "url", FileDownloadServlet.getDownloadUrl(f))).collect(Collectors.toList());
        }

    }

    @Override
    public Wrap makeWrap() {
        return new PostWrap();
    }
}
