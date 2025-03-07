package org.skyhigh.notesservice.model.dto.note;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Тело ответа по загрузке фото к заметке")
public class UploadNoteImageResponse {
    @Schema(description = "ID медиа-файла, хранящего загруженное фото")
    private UUID mediaId;

    @Schema(description = "ID заметки")
    private Long noteId;

    @Schema(description = "Дата и время обновления заметки")
    private ZonedDateTime lastChangeDate;
}
