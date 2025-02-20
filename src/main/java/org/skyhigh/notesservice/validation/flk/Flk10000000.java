package org.skyhigh.notesservice.validation.flk;

import org.skyhigh.notesservice.data.dto.TestDTO;
import org.skyhigh.notesservice.validation.exception.FlkException;

/**
 * Flk10000000 - тестовая ФЛК
 */
public class Flk10000000 extends CommonFlk {
    public Flk10000000() {
        super(
                "testDTO",
                TestDTO.class,
                "10000000",
                "Тест"
        );

        //setFlkMessage(getFlkMessage());
    }

    @Override
    public void validate(Object entity, Class<?> entityClass, String parameterName) throws FlkException {
        if (isSuitable(entityClass, parameterName)) {
            TestDTO testDTO = (TestDTO) entity;
            if (testDTO.getTest2() == null && testDTO.getTest3() == null)
                throw FlkException.builder()
                        .flkCode(getFlkCode())
                        .flkMessage(getFlkMessage())
                        .build();
        }
    }
}
