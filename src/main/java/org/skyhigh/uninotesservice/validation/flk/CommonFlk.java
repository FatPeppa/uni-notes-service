package org.skyhigh.uninotesservice.validation.flk;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class CommonFlk {
    /**
     * Наименование проверяемого параметра в проверяемом методе
     */
    private final String parameterName;

    /**
     * Тип проверяемого параметра в рамках текущей ФЛК
     */
    private final Class<?> entityClass;

    /**
     * Код ФЛК
     */
    private String flkCode;

    /**
     * Сообщение ФЛК, выдаваемое пользователю
     */
    private String flkMessage;

    public CommonFlk(
            String parameterName,
            Class<?> entityClass,
            String flkCode,
            String flkMessage
    ) {
        this.parameterName = parameterName;
        this.entityClass = entityClass;
        this.flkCode = flkCode;
        this.flkMessage = flkMessage;
    }

    protected boolean isSuitable(Class<?> entityClass, String parameterName) {
        return this.entityClass.isAssignableFrom(entityClass) && this.parameterName.equals(parameterName);
    }

    abstract protected void validate(Object entity, Class<?> entityClass, String parameterName);
}
