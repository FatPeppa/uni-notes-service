package org.skyhigh.notesservice.repository;

import org.skyhigh.notesservice.data.entity.NoteTag;
import org.skyhigh.notesservice.data.entity.NoteTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteTagRepository extends JpaRepository<NoteTag, NoteTagId> {
}
