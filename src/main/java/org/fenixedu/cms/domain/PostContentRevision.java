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

import org.fenixedu.bennu.signals.DomainObjectEvent;
import org.fenixedu.bennu.signals.Signal;

import static org.fenixedu.commons.i18n.LocalizedString.fromJson;

public class PostContentRevision extends PostContentRevision_Base implements Cloneable {


    public static final String SIGNAL_CREATED = "fenixedu.cms.postContentRevision.created";
    public static final String SIGNAL_DELETED = "fenixedu.cms.postContentRevision.deleted";


    public PostContentRevision() {
        super();
        Signal.emit(SIGNAL_CREATED, new DomainObjectEvent<>(this));
    }

    public void delete() {
        Signal.emit(SIGNAL_DELETED, this.getOid());
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
            clone.setExcerpt(getExcerpt() != null ? fromJson(getExcerpt().json()) : null);
            clone.setBody(getBody() != null ? fromJson(getBody().json()) : null);
            clone.setNext(getNext() != null ? getNext().clone(cloneCache) : null);
            return clone;
        });
    }
}
