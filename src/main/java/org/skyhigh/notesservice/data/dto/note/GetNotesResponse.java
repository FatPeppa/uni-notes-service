package org.skyhigh.notesservice.data.dto.note;

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
@Schema(description = "Тело ответа по получению заметок")
public class GetNotesResponse {
    @Schema(description = "Список заметок")
    private List<NoteContent> notes;
}
