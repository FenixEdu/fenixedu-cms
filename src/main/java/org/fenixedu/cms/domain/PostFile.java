package org.fenixedu.cms.domain;

public class PostFile extends PostFile_Base implements Comparable<PostFile> {

    public PostFile() {
        super();
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
}
