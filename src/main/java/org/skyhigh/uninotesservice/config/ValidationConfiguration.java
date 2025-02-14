package org.skyhigh.uninotesservice.config;

import org.skyhigh.uninotesservice.validation.annotation.NotEmpty;
import org.skyhigh.uninotesservice.validation.validator.field.FieldValidator;
import org.skyhigh.uninotesservice.validation.validator.field.NotEmptyValidatorImpl;
import org.skyhigh.uninotesservice.validation.validator.flk.FlkValidatorImpl;
import org.skyhigh.uninotesservice.validation.validator.param.AnnotationBasedParamValidator;
import org.skyhigh.uninotesservice.validation.validator.param.FlkBasedParamValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ValidationConfiguration {
    @Bean("AnnotationBasedParamValidator")
    public AnnotationBasedParamValidator getAnnotationBasedParamValidator() {
        Map<Class<? extends Annotation>, FieldValidator> validatorMap = new HashMap<>();
        validatorMap.put(NotEmpty.class, new NotEmptyValidatorImpl());
        return new AnnotationBasedParamValidator(validatorMap);
    }

    @Bean("FlkBasedParamValidator")
    public FlkBasedParamValidator getFlkBasedValidator() {
        return new FlkBasedParamValidator(new FlkValidatorImpl());
    }
}
