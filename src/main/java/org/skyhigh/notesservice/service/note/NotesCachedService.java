package org.skyhigh.notesservice.service.note;

import org.skyhigh.notesservice.model.entity.MediaMetadata;
import org.skyhigh.notesservice.model.entity.Note;

import java.util.List;

public interface NotesCachedService {
    List<Note> getNotesByUserIdOrderedByCreateDateDescCached(Long userId);
    Note findByIdAndUserId(Long noteId, Long userId);
    List<MediaMetadata> getMediaMetadataByNoteIdAndUserIdOrderedByCreateDateDesc(Long noteId, Long userId);
}
