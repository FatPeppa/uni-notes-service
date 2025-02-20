package org.skyhigh.notesservice.data.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Тело стандартной ошибки")
public class Error {
    @Schema(description = "8-значный код ошибки")
    protected String code;

    @Schema(description = "Имя атрибута, по которому возникла ошибка")
    protected String attributeName;

    @Schema(description = "Текст ошибки")
    protected String message;
}
