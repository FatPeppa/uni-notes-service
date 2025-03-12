package org.skyhigh.notesservice.model.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Тег заметки")
public class NoteTagObject {
    @Schema(description = "ID тега")
    private Long tagId;

    @Schema(description = "Имя тега")
    private String tagName;
}
