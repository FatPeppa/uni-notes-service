package org.skyhigh.notesservice.repository;

import jakarta.transaction.Transactional;
import org.skyhigh.notesservice.data.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query(value = "SELECT t.id, t.user_id, t.name, t.created_date, t.last_change_date FROM public.tag t JOIN public.note_tag nt ON t.id = nt.tag_id WHERE nt.note_id = ?1", nativeQuery = true)
    List<Tag> findByNoteId(Long noteId);

    @Query(value = "SELECT * FROM public.tag t WHERE t.id = ?1 AND t.user_id = ?2", nativeQuery = true)
    Tag findByTagIdAndUserId(Long tagId, Long userId);

    @Query(value = "SELECT * FROM public.tag t WHERE t.user_id = ?1 ORDER BY t.created_date DESC", nativeQuery = true)
    List<Tag> findTagsByUserIdOrderedByCreatedDateDesc(Long userId);

    @Query(value = "SELECT * FROM public.tag t WHERE t.user_id = ?2 AND t.name = ?1", nativeQuery = true)
    Tag findByNameAndUserId(String name, Long userId);

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "UPDATE public.tag SET name = ?2, last_change_date = ?3 WHERE id = ?1", nativeQuery = true)
    void updateTagNameAndLastChangeDateById(Long tagId, String name, ZonedDateTime lastChangeDate);
}
