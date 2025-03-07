package org.skyhigh.notesservice.model.dto.note;

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
@Schema(description = "Тело запроса на добавление тегов к заметке")
public class UpdateNoteTagsRequest {
    @Schema(description = "Id тегов заметки")
    private List<Long> tagIds;
}
