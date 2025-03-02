package org.skyhigh.notesservice.data.dto.note;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Тело заметки", subTypes = ExtendedNoteBody.class)
public class NoteBody implements NoteContent {
    @Schema(description = "ID заметки")
    protected Long noteId;

    @Schema(description = "ID пользователя")
    protected Long userId;

    @Schema(description = "ID категории")
    protected Long categoryId;

    @Schema(description = "Заголовок заметки")
    protected String name;

    @Schema(description = "Выжимка текста заметки")
    protected String textExtraction;

    @Schema(description = "ID текстового файла заметки")
    protected UUID mediaId;

    @Schema(description = "Список ID приложенных к заметке фото")
    protected List<UUID> imageIdList;

    @Schema(description = "Дата создания заметки")
    protected ZonedDateTime createdDate;

    @Schema(description = "Дата обновления заметки")
    protected ZonedDateTime lastChangeDate;

    @Override
    public int compareTo(NoteContent o) {
        if (this.createdDate.isBefore(((NoteBody) o).createdDate))
            return -1;
        else if (this.createdDate.isAfter(((NoteBody) o).createdDate))
            return 1;
        else
            return 0;
    }
}
