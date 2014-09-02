package org.fenixedu.bennu.cms.domain;

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

}
