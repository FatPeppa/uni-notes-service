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
@Table(name = "note", schema = "public")
public class Note implements Comparable<Note> {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "public.note_id_seq")
    @SequenceGenerator(name = "public.note_id_seq", sequenceName = "public.note_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "name")
    private String name;

    @Column(name = "text_extraction")
    private String textExtraction;

    @Column(name = "media_id")
    private UUID mediaId;

    @Column(name = "created_date", nullable = false)
    private ZonedDateTime createdDate;

    @Column(name = "last_change_date", nullable = false)
    private ZonedDateTime lastChangeDate;

    @Override
    public int compareTo(Note o) {
        if (this.createdDate.isBefore(o.createdDate))
            return -1;
        else if (this.createdDate.isAfter(o.createdDate))
            return 1;
        else
            return 0;
    }
}
