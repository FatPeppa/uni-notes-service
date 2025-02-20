package org.skyhigh.notesservice.config;

import org.skyhigh.notesservice.validation.annotation.Email;
import org.skyhigh.notesservice.validation.annotation.NotEmpty;
import org.skyhigh.notesservice.validation.annotation.Size;
import org.skyhigh.notesservice.validation.validator.field.EmailValidatorImpl;
import org.skyhigh.notesservice.validation.validator.field.FieldValidator;
import org.skyhigh.notesservice.validation.validator.field.NotEmptyValidatorImpl;
import org.skyhigh.notesservice.validation.validator.field.SizeValidatorImpl;
import org.skyhigh.notesservice.validation.validator.flk.FlkValidatorImpl;
import org.skyhigh.notesservice.validation.validator.param.AnnotationBasedParamValidator;
import org.skyhigh.notesservice.validation.validator.param.FlkBasedParamValidator;
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
        validatorMap.put(Size.class, new SizeValidatorImpl());
        validatorMap.put(Email.class, new EmailValidatorImpl());
        return new AnnotationBasedParamValidator(validatorMap);
    }

    @Bean("FlkBasedParamValidator")
    public FlkBasedParamValidator getFlkBasedValidator() {
        return new FlkBasedParamValidator(new FlkValidatorImpl());
    }
}
