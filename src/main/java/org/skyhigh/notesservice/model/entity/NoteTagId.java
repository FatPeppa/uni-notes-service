package org.skyhigh.notesservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteTagId implements Serializable {
    public String noteId;
    public String tagId;
}
