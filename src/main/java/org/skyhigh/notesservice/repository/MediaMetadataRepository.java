package org.skyhigh.notesservice.repository;

import jakarta.transaction.Transactional;
import org.skyhigh.notesservice.model.entity.MediaMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MediaMetadataRepository extends JpaRepository<MediaMetadata, UUID> {
    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "UPDATE public.media_metadata SET name = ?2, created_date = ?3 WHERE id = ?1", nativeQuery = true)
    void updateNameAndCreatedDateById(UUID mediaId, String name, ZonedDateTime createdDate);

    @Query(value = "SELECT mm.id, mm.user_id, mm.name, mm.file_type, mm.created_date, mm.url FROM public.media_metadata mm JOIN public.note_media nm ON mm.id = nm.media_id WHERE nm.note_id = ?1 AND mm.user_id = ?2", nativeQuery = true)
    List<MediaMetadata> getMediaMetadataByNoteIdAndUserIdOrderByCreatedDateDesc(Long noteId, Long userId);
}
