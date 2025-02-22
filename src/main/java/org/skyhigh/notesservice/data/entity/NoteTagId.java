package org.skyhigh.notesservice.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteTagId implements Serializable {
    private String noteId;
    private String tagId;
}
