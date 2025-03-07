package org.skyhigh.notesservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_deactivated_token", schema = "public")
public class DeactivatedToken {
    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
}
