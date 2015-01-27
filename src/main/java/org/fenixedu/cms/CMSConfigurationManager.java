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
package org.fenixedu.cms;

import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;

import com.google.common.base.Strings;

public class CMSConfigurationManager {
    @ConfigurationManager(description = "General CMS Configuration")
    public interface ConfigurationProperties {

        @ConfigurationProperty(key = "theme.development.directory",
                description = "When you are developing a theme, you can set this to the directory for online testing")
        public String themeDevelopmentDirectory();
    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }

    private static final boolean devMode = CoreConfiguration.getConfiguration().developmentMode()
            && !Strings.isNullOrEmpty(getConfiguration().themeDevelopmentDirectory());

    public static boolean isInThemeDevelopmentMode() {
        return devMode;
    }
}
