package org.fenixedu.bennu.cms.domain;

/**
 * Site templates know how to build the template structure and basic information for sites.
 */
public interface SiteTemplate {
    /**
     * Creates the basic structure for the given site and populates it with some initial information.
     * 
     * @param site
     */
    public void makeIt(Site site);
}
