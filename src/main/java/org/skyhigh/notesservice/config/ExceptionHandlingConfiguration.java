package org.skyhigh.notesservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties
public class ExceptionHandlingConfiguration {
    @Value("${debug}")
    private boolean debugMode;

    @Bean("DebugMode")
    public boolean getDebugMode() {
        return debugMode;
    }
}
