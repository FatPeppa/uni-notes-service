package org.skyhigh.notesservice.model.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoteBodyObject {
    private Long noteId;
    private Long userId;
    private String name;
    private NoteCategoryObject noteCategoryObject;
}
