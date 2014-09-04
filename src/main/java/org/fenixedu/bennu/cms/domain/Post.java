package org.fenixedu.bennu.cms.domain;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.bennu.cms.domain.component.Component;
import org.fenixedu.bennu.cms.exceptions.CmsDomainException;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.AnyoneGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

import com.google.common.collect.Lists;

/**
 * A post models a given content to be presented to the user.
 */
public class Post extends Post_Base {

    public static final Comparator<? super Post> CREATION_DATE_COMPARATOR = (o1, o2) -> o2.getCreationDate().compareTo(
            o1.getCreationDate());

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
            setSlug(StringNormalizer.slugify(name.getContent()));
        }
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

    public boolean hasReferedSubjectPeriod() {
        return getReferedSubjectBegin() != null && getReferedSubjectEnd() != null;
    }

    public boolean isInPublicationPeriod() {
        boolean inBegin = getPublicationBegin() == null || getPublicationBegin().isAfterNow();
        boolean inEnd = getPublicationEnd() == null || getPublicationEnd().isBeforeNow();
        return inBegin && inEnd;
    }

    public boolean isInReferedSubjectPeriod() {
        boolean inBegin = getReferedSubjectBegin() == null || getReferedSubjectBegin().isAfterNow();
        boolean inEnd = getReferedSubjectEnd() == null || getReferedSubjectEnd().isBeforeNow();
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
     * @param group
     *            the group of people who can view this site
     */
    @Atomic
    public void setCanViewGroup(Group group) {
        setViewGroup(group.toPersistentGroup());
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

    private void fixOrder(List<PostFile> sortedItems) {
        for (int i = 0; i < sortedItems.size(); ++i) {
            sortedItems.get(i).setIndex(i);
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
    }

    public Attachments getAttachments() {
        return new Attachments();
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
}