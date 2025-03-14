package org.skyhigh.notesservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "media_type", schema = "public")
public class MediaType {
    @Id
    @Column(name = "code")
    @Enumerated(EnumType.STRING)
    private MediaTypeEnum code;
}
