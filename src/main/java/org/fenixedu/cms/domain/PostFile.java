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

import org.fenixedu.bennu.core.signals.DomainObjectEvent;
import org.fenixedu.bennu.core.signals.Signal;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.io.servlet.FileDownloadServlet;
import org.fenixedu.cms.domain.wraps.Wrap;
import org.fenixedu.cms.domain.wraps.Wrappable;

import java.io.IOException;
import java.util.Comparator;

public class PostFile extends PostFile_Base implements Comparable<PostFile>, Wrappable, Cloneable {

    public static final String SIGNAL_CREATED = "fenixedu.cms.postFile.created";
    public static final String SIGNAL_DELETED = "fenixedu.cms.postFile.deleted";
    public static final String SIGNAL_EDITED = "fenixedu.cms.postFile.edited";

    public static final Comparator<PostFile> NAME_COMPARATOR =
            Comparator.comparing(postFile->postFile.getFiles().getDisplayName());

    public PostFile(Post post, GroupBasedFile file, boolean isEmbedded, int index) {
        setSite(post.getSite());
        setPost(post);
        setFiles(file);
        setIsEmbedded(isEmbedded);
        setIndex(index);
        Signal.emit(SIGNAL_CREATED,new DomainObjectEvent<>(this));
    }

    @Override
    public PostFile clone(CloneCache cloneCache) {
        return cloneCache.getOrClone(this, obj -> {
            
            try {
                GroupBasedFile fileClone  = new GroupBasedFile(getFiles().getDisplayName(), getFiles().getFilename(),
                        getFiles().getStream(), getFiles().getAccessGroup());
                return new PostFile(getPost(), fileClone, getIsEmbedded(), getIndex());
            } catch (IOException e) {
                throw new RuntimeException("Could not clone file " + getFiles().getDisplayName());
            }
            
        });
    }

    @Override
    public void setIsEmbedded(boolean isEmbedded) {
        if (isEmbedded) {
            getFiles().setAccessGroup(getPost().getCanViewGroup());
        }
        super.setIsEmbedded(isEmbedded);
    }

    @Override
    public int compareTo(PostFile o) {
        return this.getIndex().compareTo(o.getIndex());
    }

    public void delete() {
        Signal.emit(SIGNAL_DELETED, this.getOid());
        setSite(null);
        setPost(null);
        setFiles(null);
        deleteDomainObject();
    }

    public String getEditUrl() {
        return CoreConfiguration.getConfiguration().applicationUrl() + "/cms/media/" + getSite().getSlug() + "/"
                        + getExternalId() + "/edit";
    }

    @Override
    public Wrap makeWrap() {
        return new PostFileWrap();
    }

    public class PostFileWrap extends Wrap {
        public String getName() {
            return PostFile.this.getFiles().getDisplayName();
        }

        public String getContentType() {
            return PostFile.this.getFiles().getContentType();
        }

        public String getUrl() {
            return FileDownloadServlet.getDownloadUrl(PostFile.this.getFiles());
        }
    }
}
