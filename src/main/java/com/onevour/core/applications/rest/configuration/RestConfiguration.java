package com.onevour.core.applications.rest.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @Configuration
public class RestConfiguration {

    private Logger log = LoggerFactory.getLogger(RestConfiguration.class);

    /*
    @Bean
    public RestConfigBaseKey restConfigBaseKey(@Autowired ConfigRepository configRepository) {
        return new RestConfigBaseKey() {
            @Override
            public String getValue(String key) {
                Config config = configRepository.findByKey(key).orElse(null);
                assert config != null;
                return config.getValue();
            }
        };
    }
     */

}
