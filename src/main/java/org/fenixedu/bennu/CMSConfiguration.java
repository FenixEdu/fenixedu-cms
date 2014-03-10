package org.fenixedu.bennu;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
@ComponentScan("org.fenixedu.cms")
public class CMSConfiguration {
    private static final int FILE_MAX_SIZE_IN_BYTES = 10 * 1024 * 1024;
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setMaxUploadSize(FILE_MAX_SIZE_IN_BYTES);
        return commonsMultipartResolver;
    }
}
