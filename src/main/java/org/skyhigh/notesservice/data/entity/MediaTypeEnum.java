package org.skyhigh.notesservice.data.entity;

import lombok.Getter;

@Getter
public enum MediaTypeEnum {
    NOTE_TXT(new String[] {"text/plain"}),
    PNG(new String[] {"image/png"}),
    JPEG(new String[] {"image/jpeg"}),
    HEIC(new String[] {"image/heic"}),
    NOT_SUPPORTED(new String[] {"*"});

    private final String[] fileTypes;

    private MediaTypeEnum(String[] fileTypes) {
        this.fileTypes = fileTypes;
    }
}
