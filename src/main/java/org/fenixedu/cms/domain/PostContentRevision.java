package org.fenixedu.cms.domain;

import static org.fenixedu.commons.i18n.LocalizedString.fromJson;

public class PostContentRevision extends PostContentRevision_Base implements Cloneable {

    public PostContentRevision() {
        super();
    }

    public void delete() {
        setNext(null);
        setPrevious(null);
        setPost(null);
        setIsLastestRevision(null);
        setCreatedBy(null);
        deleteDomainObject();

    }

    /**
     * recursively clones the current and all the next elements of the chain
     *
     * @return a clone of the post content revision with the same content and metadata
     * @throws CloneNotSupportedException
     */
    @Override
    public PostContentRevision clone(CloneCache cloneCache) {
        return cloneCache.getOrClone(this, obj -> {
            PostContentRevision clone = new PostContentRevision();
            cloneCache.setClone(PostContentRevision.this, clone);
            clone.setPost(getPost());
            clone.setCreatedBy(getCreatedBy());
            clone.setRevisionDate(getRevisionDate());
            clone.setIsLastestRevision(getIsLastestRevision());
            clone.setBody(getBody() != null ? fromJson(getBody().json()) : null);
            clone.setNext(getNext() != null ? getNext().clone(cloneCache) : null);
            return clone;
        });
    }
}
