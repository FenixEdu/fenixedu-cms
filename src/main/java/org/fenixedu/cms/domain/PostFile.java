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

import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.io.servlets.FileDownloadServlet;
import org.fenixedu.cms.domain.wraps.Wrap;
import org.fenixedu.cms.domain.wraps.Wrappable;

public class PostFile extends PostFile_Base implements Comparable<PostFile>, Wrappable {

    public PostFile(Post post, GroupBasedFile file, boolean isEmbedded, int index) {
	setPost(post);
	setFiles(file);
	setIsEmbedded(isEmbedded);
	setIndex(index);
    }

    @Override
    public void setIsEmbedded(Boolean isEmbedded) {
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
	setFiles(null);
	setPost(null);
	deleteDomainObject();
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
