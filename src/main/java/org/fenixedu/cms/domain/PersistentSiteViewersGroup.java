package org.fenixedu.cms.domain;

import org.fenixedu.bennu.core.groups.Group;

public final class PersistentSiteViewersGroup extends PersistentSiteViewersGroup_Base {

    PersistentSiteViewersGroup(Site site) {
        super();
        setSite(site);
    }

    @Override
    public Group toGroup() {
        return SiteViewersGroup.get(getSite());
    }

    public void delete() {
        this.setSite(null);
        this.setRoot(null);
        this.setNegation(null);
        this.deleteDomainObject();
    }

}
