package org.skyhigh.notesservice.data.dto.note;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.skyhigh.notesservice.validation.annotation.NotEmpty;
import org.skyhigh.notesservice.validation.annotation.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Тело запроса по обновлению тела заметки")
public class UpdateNoteBodyRequest {
    @Schema(description = "ID категории")
    private Long categoryId;

    @Schema(description = "Заголовок заметки")
    @NotEmpty
    @Size(max = "255")
    private String name;
}
