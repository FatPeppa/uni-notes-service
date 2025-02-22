package org.skyhigh.notesservice.repository;

import org.skyhigh.notesservice.data.entity.NoteMedia;
import org.skyhigh.notesservice.data.entity.NoteMediaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteMediaRepository extends JpaRepository<NoteMedia, NoteMediaId> {
}
