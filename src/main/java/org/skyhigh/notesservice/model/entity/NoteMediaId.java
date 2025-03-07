package org.skyhigh.notesservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteMediaId implements Serializable {
    private Long noteId;
    private UUID mediaId;
}
