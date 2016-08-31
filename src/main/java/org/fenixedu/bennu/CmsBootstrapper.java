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
package org.fenixedu.bennu;

import org.fenixedu.bennu.core.bootstrap.AdminUserBootstrapper;
import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrap;
import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrapper;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.cms.domain.CmsSettings;
import org.fenixedu.cms.domain.DefaultRoles;

import pt.ist.fenixframework.FenixFramework;

@Bootstrapper(bundle = "resources.CmsResources", name = "application.title.cms.bootstrapper", after = AdminUserBootstrapper.class,
        sections = {})
public class CmsBootstrapper {

    @Bootstrap
    public static void bootstrapCms() {
        if (Bennu.getInstance().getCmsSettings() == null) {
            FenixFramework.atomic(() -> Bennu.getInstance().setCmsSettings(new CmsSettings()));
        }
        DefaultRoles.getInstance().init();
    }

}
