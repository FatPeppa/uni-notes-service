package org.skyhigh.notesservice.model.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Направление сортировки")
public enum SortDirection {
    ASC,
    DESC
}
