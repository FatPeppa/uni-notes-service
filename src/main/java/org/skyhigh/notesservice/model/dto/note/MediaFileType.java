package org.skyhigh.notesservice.model.dto.note;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Тип файла")
public enum MediaFileType {
    TXT(new String[] {"text/plain"}),
    PNG(new String[] {"image/png"}),
    JPEG(new String[] {"image/jpeg"}),
    HEIC(new String[] {"image/heic"});

    private final String[] fileTypes;

    private MediaFileType(String[] fileTypes) {
        this.fileTypes = fileTypes;
    }
}
