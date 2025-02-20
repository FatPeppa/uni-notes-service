package org.skyhigh.notesservice.validation.aspect;

import java.lang.annotation.*;

@Documented
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidParams {
    String[] flks() default "";
}

