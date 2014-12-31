package org.fenixedu.cms.domain;

public interface Sluggable {
    public String getSlug();
    public void setSlug(String slug);
    boolean isValidSlug(String slug);
}
