package org.fenixedu.bennu.cms;

import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;

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
}
