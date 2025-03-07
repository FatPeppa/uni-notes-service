package org.skyhigh.notesservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "note_media", schema = "public")
public class NoteMedia {
    @Column(name = "note_id", nullable = false)
    private Long noteId;

    @Id
    @Column(name = "media_id", nullable = false)
    private UUID mediaId;

    @Column(name = "created_date", nullable = false)
    private ZonedDateTime createdDate;
}
