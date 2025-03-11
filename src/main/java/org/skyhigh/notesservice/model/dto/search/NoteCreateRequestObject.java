package org.skyhigh.notesservice.model.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoteCreateRequestObject {
    private NoteBodyObject noteBodyObject;
    private ZonedDateTime createdDate;
    private ZonedDateTime lastChangeDate;
}
