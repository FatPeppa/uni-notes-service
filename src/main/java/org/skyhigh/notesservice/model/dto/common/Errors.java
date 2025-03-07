package org.skyhigh.notesservice.model.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Тело ответа с массивом стандартных ошибок")
public class Errors {
    @Schema(description = "Массив стандартных ошибок")
    private List<Error> errors;
}
