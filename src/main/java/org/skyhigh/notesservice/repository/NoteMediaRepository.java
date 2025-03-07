package org.skyhigh.notesservice.repository;

import jakarta.transaction.Transactional;
import org.skyhigh.notesservice.model.entity.NoteMedia;
import org.skyhigh.notesservice.model.entity.NoteMediaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NoteMediaRepository extends JpaRepository<NoteMedia, NoteMediaId> {
    @Query(value = "SELECT count(*) FROM public.note_media nm WHERE nm.note_id = ?1", nativeQuery = true)
    Integer countByNoteId(Long noteId);

    @Query(value = "SELECT * FROM public.note_media nm WHERE nm.note_id = ?1", nativeQuery = true)
    List<NoteMedia> findByNoteId(Long noteId);

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "DELETE FROM public.note_media WHERE note_id = ?1", nativeQuery = true)
    void deleteByNoteId(Long noteId);

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "DELETE FROM public.note_media WHERE note_id = ?1 AND media_id = ?2", nativeQuery = true)
    void deleteByNoteIdAndMediaId(Long noteId, UUID mediaId);

    @Query(value = "SELECT * FROM public.note_media nm WHERE nm.note_id = ?1 AND nm.media_id = ?2", nativeQuery = true)
    NoteMedia findByNoteIdAndMediaId(Long noteId, UUID mediaId);

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "INSERT INTO public.note_media VALUES (?1, ?2, ?3)", nativeQuery = true)
    void saveEntity(Long noteId, UUID mediaId, ZonedDateTime createdDate);
}
