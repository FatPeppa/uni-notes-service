package org.skyhigh.notesservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.skyhigh.notesservice.model.dto.common.SortDirection;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_properties", schema = "public")
public class UserProperties {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "public.user_properties_seq")
    @SequenceGenerator(name = "public.user_properties_seq", sequenceName = "public.user_properties_seq", allocationSize = 1)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "last_notes_created_date_sort_direction", nullable = false)
    private SortDirection lastNotesCreatedDateSortDirection;
}
