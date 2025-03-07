package org.skyhigh.notesservice.model.dto.note;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.skyhigh.notesservice.model.dto.tag.SimpleTagBody;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Расширенное тело заметки")
public class ExtendedNoteBody extends NoteBody implements NoteContent {
    @Schema(description = "Имя категории")
    private String categoryName;

    @Schema(description = "Список тегов заметки")
    private List<SimpleTagBody> tags;

    public ExtendedNoteBody(
            Long id,
            Long userId,
            Long categoryId,
            String categoryName,
            String name,
            String textExtraction,
            List<SimpleTagBody> tags,
            UUID mediaId,
            List<UUID> imageIdList,
            ZonedDateTime createdDate,
            ZonedDateTime lastChangeDate
    ) {
        super(
                id,
                userId,
                categoryId,
                name,
                textExtraction,
                mediaId,
                imageIdList,
                createdDate,
                lastChangeDate
        );
        this.categoryName = categoryName;
        this.tags = tags;
    }

    @Override
    public int compareTo(NoteContent o) {
        if (this.createdDate.isBefore(((ExtendedNoteBody) o).createdDate))
            return -1;
        else if (this.createdDate.isAfter(((ExtendedNoteBody) o).createdDate))
            return 1;
        else
            return 0;
    }
}
