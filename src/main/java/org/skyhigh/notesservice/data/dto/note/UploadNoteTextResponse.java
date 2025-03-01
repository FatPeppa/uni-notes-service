package org.skyhigh.notesservice.data.dto.note;

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
@Schema(description = "Тело ответа по загрузке текста к заметке")
public class UploadNoteTextResponse {
    @Schema(description = "ID медиа-файла, хранящего загруженный текст заметки")
    private UUID mediaId;

    @Schema(description = "ID заметки")
    private Long noteId;

    @Schema(description = "Дата и время обновления заметки")
    private ZonedDateTime lastChangeDate;
}
