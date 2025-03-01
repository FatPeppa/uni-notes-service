package org.skyhigh.notesservice.repository;

import jakarta.transaction.Transactional;
import org.skyhigh.notesservice.data.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "UPDATE public.note SET media_id = ?2, last_change_date = ?3 WHERE id = ?1", nativeQuery = true)
    void updateMediaIdAndLastChangeDateById(Long noteId, UUID mediaId, ZonedDateTime lastChangeDate);

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "UPDATE public.note SET media_id = ?2, text_extraction = ?3, last_change_date = ?4 WHERE id = ?1", nativeQuery = true)
    void updateMediaIdAndTextExtractionAndLastChangeDateById(Long noteId, UUID mediaId, String textExtraction, ZonedDateTime lastChangeDate);

    @Query(value = "SELECT * FROM public.note n WHERE n.user_id = ?1 AND n.name = ?2 LIMIT 1", nativeQuery = true)
    Note findByUserIdAndName(Long userId, String name);

    @Query(value = "SELECT * FROM public.note n WHERE n.id = ?1 AND n.user_id = ?2 LIMIT 1", nativeQuery = true)
    Note findByIdAndUserId(Long noteId, Long userId);

    @Query(value = "SELECT * FROM public.note n WHERE n.user_id = ?1", nativeQuery = true)
    List<Note> findAllByUserId(Long userId);

    @Query(value = "SELECT * FROM public.note n WHERE n.user_id = ?1 ORDER BY n.created_date DESC", nativeQuery = true)
    List<Note> findAllByUserIdOrderByCreatedDateDesc(Long userId);

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "UPDATE public.note SET last_change_date = ?2 WHERE id = ?1", nativeQuery = true)
    void updateLastChangeDateById(Long noteId, ZonedDateTime lastChangeDate);

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "UPDATE public.note SET category_id = ?2, name = ?3, last_change_date = ?4 WHERE id = ?1", nativeQuery = true)
    void updateCategoryIdAndNameAndLastChangeDateById(Long noteId, Long categoryId, String name, ZonedDateTime lastChangeDate);

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "UPDATE public.note SET category_id = ?2, last_change_date = ?3 WHERE id = ?1", nativeQuery = true)
    void updateCategoryIdAndLastChangeDateById(Long noteId, Long categoryId, ZonedDateTime lastChangeDate);

    @Query(value = "SELECT * FROM public.note n WHERE n.user_id = ?1 AND n.category_id = ?2", nativeQuery = true)
    List<Note> findByUserIdAndCategoryId(Long userId, Long categoryId);
}
