package org.skyhigh.notesservice.model.dto.note;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Тип детализации ответа поиска заметок")
public enum NoteSearchResponseDetailType {
    FULL,
    WITHOUT_CONTENT,
    WITHOUT_TEXT_EXTRACTION,
    WITHOUT_TAGS,
    WITHOUT_MEDIA_IDS,
    WITHOUT_TAGS_AND_MEDIA_IDS
}
