package org.skyhigh.notesservice.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "note_tag")
@IdClass(NoteTagId.class)
public class NoteTag {
    @Id
    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    @Id
    @Column(name = "note_id", nullable = false)
    private Long noteId;

    @Column(name = "created_date", nullable = false)
    private ZonedDateTime createdDate;
}
