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
@Schema(description = "Тело ответа по получению метаданных фото заметки")
public class GetNoteMediaMetadataResponse {
    @Schema(description = "Список тел метаданных файлов")
    List<MediaMetadataBody> metadataBodyList;
}
