package org.fenixedu.cms.domain;

public class PostContentRevision extends PostContentRevision_Base {
    
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
    
}
