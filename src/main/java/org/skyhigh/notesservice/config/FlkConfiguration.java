package org.skyhigh.notesservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableConfigurationProperties
@PropertySource("classpath:application.yml")
public class FlkConfiguration {
    @Value("${flk.active}")
    private String[] activeFlkNames;

    @Bean("ActiveFlkList")
    public List<String> getActiveFlkList() {
        return Arrays.asList(activeFlkNames);
    }
}
