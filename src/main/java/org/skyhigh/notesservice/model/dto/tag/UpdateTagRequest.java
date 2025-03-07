package org.skyhigh.notesservice.model.dto.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.skyhigh.notesservice.validation.annotation.NotEmpty;
import org.skyhigh.notesservice.validation.annotation.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Тело запроса на обновление тега")
public class UpdateTagRequest {
    @Schema(description = "Имя тега; до 50 символов")
    @NotEmpty
    @Size(max = "50")
    private String name;
}
