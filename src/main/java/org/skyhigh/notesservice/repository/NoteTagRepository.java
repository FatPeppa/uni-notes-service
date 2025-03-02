package org.skyhigh.notesservice.repository;

import jakarta.transaction.Transactional;
import org.skyhigh.notesservice.data.entity.NoteTag;
import org.skyhigh.notesservice.data.entity.NoteTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface NoteTagRepository extends JpaRepository<NoteTag, NoteTagId> {
    @Query(value = "SELECT nt.tag_id, nt.note_id, nt.created_date FROM public.note_tag nt WHERE nt.note_id = ?1 AND nt.tag_id = ?2", nativeQuery = true)
    NoteTag findByNoteIdAndTagId(Long noteId, Long tagId);

    List<NoteTag> findByNoteId(Long noteId);

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "DELETE FROM public.note_tag WHERE note_id = ?1", nativeQuery = true)
    void deleteByNoteId(Long noteId);

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "INSERT INTO public.note_tag VALUES (?2, ?1, ?3)", nativeQuery = true)
    void saveEntity(Long tagId, Long noteId, ZonedDateTime createdDate);
}
