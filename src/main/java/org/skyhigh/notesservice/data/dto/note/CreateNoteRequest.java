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
@Schema(description = "Тело ответа по загрузке фото к заметке")
public class CreateNoteRequest {
    @Schema(description = "ID категории")
    private Long categoryId;

    @Schema(description = "Заголовок заметки; до 255 символов")
    @NotEmpty
    @Size(max = "255")
    private String name;
}
