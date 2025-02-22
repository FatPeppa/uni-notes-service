package org.skyhigh.notesservice.data.entity;

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
@Table(name = "note")
public class Note {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "note_id_seq")
    @SequenceGenerator(name = "note_id_seq", sequenceName = "note_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "media_id", nullable = false)
    private UUID mediaId;

    @Column(name = "created_date", nullable = false)
    private ZonedDateTime createdDate;

    @Column(name = "last_change_date", nullable = false)
    private ZonedDateTime lastChangeDate;
}
