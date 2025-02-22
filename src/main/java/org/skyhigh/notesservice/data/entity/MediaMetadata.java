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
@Table(name = "media_metadata")
public class MediaMetadata {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "file_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaTypeEnum fileType;

    @Column(name = "created_date", nullable = false)
    private ZonedDateTime createdDate;

    @Column(name = "url", nullable = false)
    private String url;
}
