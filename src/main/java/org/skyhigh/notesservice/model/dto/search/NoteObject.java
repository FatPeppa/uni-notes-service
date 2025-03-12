package org.skyhigh.notesservice.model.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Заметка")
public class NoteObject implements Comparable<NoteObject> {
    @Schema(description = "ID заметки")
    private Long noteId;

    @Schema(description = "ID пользователя")
    private Long userId;

    @Schema(description = "Заголовок заметки")
    private String name;

    @Schema(description = "Категория заметки")
    private NoteCategoryObject noteCategoryObject;

    @Schema(description = "Список тегов заметки")
    private List<NoteTagObject> noteTagObjects;

    @Schema(description = "Выжимка текста заметки")
    private String textExtraction;

    @Schema(description = "ID текстового файла заметки")
    private UUID mediaId;

    @Schema(description = "Список ID файлов с фото заметки")
    private List<UUID> imageIds;

    @Schema(description = "Дата и время создания заметки")
    private ZonedDateTime createdDate;

    @Schema(description = "Дата и время обновления заметки")
    private ZonedDateTime lastChangeDate;

    @Schema(description = "Содержимое заметки")
    private byte[] content;

    @Override
    public int compareTo(NoteObject o) {
        if (this.createdDate.isBefore(o.createdDate))
            return -1;
        else if (this.createdDate.isAfter(o.createdDate))
            return 1;
        else
            return 0;
    }
}
