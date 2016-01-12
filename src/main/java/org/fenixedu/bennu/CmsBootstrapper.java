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
