package org.skyhigh.notesservice.model.dto.search;

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
@Schema(description = "Тело ответа на запрос по поиску заметок")
public class FindNotesResponse {
    @Schema(description = "Список найденных заметок")
    List<NoteObject> notes;
}
