package org.skyhigh.uninotesservice.validation.validator.param;

import jakarta.validation.ValidationException;
import org.skyhigh.uninotesservice.validation.validator.flk.FlkValidator;

import java.util.List;

/**
 * Валидатор параметров метода с помощью ФЛК
 */
public class FlkBasedParamValidator {
    private final FlkValidator flkValidator;

    public FlkBasedParamValidator(FlkValidator flkValidator) {
        this.flkValidator = flkValidator;
    }

    public void validate(Object param, String parameterName, List<String> currentMethodActiveFlk) {
        if (param == null)
            throw new ValidationException("Passed param is null");
        Class<?> clazz = param.getClass();
        flkValidator.validate(param, clazz, parameterName, currentMethodActiveFlk);
    }
}
