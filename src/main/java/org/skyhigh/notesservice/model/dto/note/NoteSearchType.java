package org.skyhigh.notesservice.model.dto.note;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Тип поиска заметок")
public enum NoteSearchType {
    NON_NORMALIZED,
    NON_NORMALIZED_FULL_TEXT,
    NORMALIZED,
    NORMALIZED_FULL_TEXT
}
