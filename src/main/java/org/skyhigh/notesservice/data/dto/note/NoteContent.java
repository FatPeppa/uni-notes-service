package org.skyhigh.notesservice.data.dto.note;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Schema(
        description = "Содержимое заметки",
        subTypes = {NoteBody.class, ExtendedNoteBody.class}
)
public interface NoteContent extends Serializable, Comparable<NoteContent> {
    ZonedDateTime getCreatedDate();
}
