package org.skyhigh.notesservice.validation.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.skyhigh.notesservice.validation.exception.FlkException;
import org.skyhigh.notesservice.validation.exception.MultipleFlkException;
import org.skyhigh.notesservice.validation.exception.NullParameterException;
import org.skyhigh.notesservice.validation.validator.param.AnnotationBasedParamValidator;
import org.skyhigh.notesservice.validation.validator.param.FlkBasedParamValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class MethodParamValidationAspect {
    private final AnnotationBasedParamValidator annotationBasedValidator;
    private final FlkBasedParamValidator flkBasedValidator;
    private final List<String> activeFlkList;

    public MethodParamValidationAspect(
            @Qualifier("AnnotationBasedParamValidator") AnnotationBasedParamValidator annotationBasedValidator,
            @Qualifier("FlkBasedParamValidator") FlkBasedParamValidator flkBasedValidator,
            @Qualifier("ActiveFlkList") List<String> activeFlkList
    ) {
        this.annotationBasedValidator = annotationBasedValidator;
        this.flkBasedValidator = flkBasedValidator;
        this.activeFlkList = activeFlkList;
    }

    @Before(value = "@annotation(org.skyhigh.notesservice.validation.aspect.ValidParams)")
    public void validateParameters(JoinPoint joinPoint) {
        MultipleFlkException multipleFlkException = null;

        String[] parameterNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        List<String> methodFlks = Arrays.stream(((MethodSignature) joinPoint.getSignature())
                .getMethod().getAnnotation(ValidParams.class).flks())
                .filter(x-> !x.isEmpty()).toList();
        List<String> currentMethodActiveFlks = activeFlkList.stream()
                .filter(x -> !methodFlks.contains(x)).toList();

        for (int i = 0; i < parameterNames.length; i++) {
            try {
                annotationBasedValidator.validate(joinPoint.getArgs()[i]);
            } catch (NullParameterException e) {
                if (multipleFlkException == null)
                    multipleFlkException = new MultipleFlkException();
                multipleFlkException.addFlkException(new FlkException(
                        e.getCode(),
                        e.getMessage(),
                        e.getParameterName()
                ));
            }
            try {
                flkBasedValidator.validate(joinPoint.getArgs()[i], parameterNames[i], currentMethodActiveFlks);
            } catch (MultipleFlkException e) {
                if (multipleFlkException == null)
                    multipleFlkException = new MultipleFlkException();
                multipleFlkException.addAllExceptions(e.getFlkExceptions());
            } catch (FlkException e) {
                if (multipleFlkException == null)
                    multipleFlkException = new MultipleFlkException();
                multipleFlkException.addFlkException(e);
            }
        }

        if (multipleFlkException != null)
            throw multipleFlkException;
    }
}
