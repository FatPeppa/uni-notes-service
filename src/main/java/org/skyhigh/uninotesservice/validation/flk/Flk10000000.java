package org.skyhigh.uninotesservice.validation.flk;

import org.skyhigh.uninotesservice.data.dto.TestDTO;
import org.skyhigh.uninotesservice.validation.exception.FlkException;

/**
 * Flk10000000 - ФЛК на проверку длины логина
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
