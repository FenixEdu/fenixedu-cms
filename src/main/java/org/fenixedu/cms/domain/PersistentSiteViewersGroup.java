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
