package org.skyhigh.notesservice.data.dto.tag;

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
@Schema(description = "Тело запроса на создание тега")
public class CreateTagRequest {
    @Schema(description = "Имя тега; до 50 символов")
    @NotEmpty
    @Size(max = "50")
    private String name;
}
