package org.skyhigh.notesservice.validation.validator.flk;

import org.skyhigh.notesservice.validation.exception.FlkException;
import org.skyhigh.notesservice.validation.exception.MultipleFlkException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Валидатор объектов на основе списка номеров ФЛК, которые необходимо выполнить
 */
public class FlkValidatorImpl implements FlkValidator {
    @Override
    public void validate(Object entity, Class<?> entityClass, String parameterName,
                         List<String> currentMethodActiveFlk) {
        List<FlkException> flkExceptions = new ArrayList<>();

        currentMethodActiveFlk.forEach(activeFlkName -> {
            try {
                Class<?> clazz = Class.forName("org.skyhigh.notesservice.validation.flk." + activeFlkName);
                Method validationMethod = clazz.getMethod(
                        "validate",
                        Object.class,
                        Class.class,
                        String.class
                );
                Object flkObject = clazz.getConstructor().newInstance();
                validationMethod.invoke(flkObject, entity, entityClass, parameterName);
            } catch (ClassNotFoundException | NoSuchMethodException |
                     IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                flkExceptions.add((FlkException) e.getTargetException());
            }
        });

        if (!flkExceptions.isEmpty())
            throw new MultipleFlkException(flkExceptions);
    }
}
