package org.skyhigh.notesservice.repository;

import jakarta.transaction.Transactional;
import org.skyhigh.notesservice.data.entity.UserProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserPropertiesRepository extends JpaRepository<UserProperties, Long>  {
    UserProperties findByUserId(Long userId);

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "UPDATE public.user_properties SET last_notes_created_date_sort_direction = ?2 WHERE user_id = ?1", nativeQuery = true)
    void updateLastNotesCreatedDateSortDirectionByUserId(Long userId, String sortDirectionName);
}
