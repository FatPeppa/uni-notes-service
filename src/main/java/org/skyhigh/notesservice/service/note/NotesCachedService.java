package org.skyhigh.notesservice.service.note;

import org.skyhigh.notesservice.data.entity.Note;

import java.util.List;

public interface NotesCachedService {
    List<Note> getNotesByUserIdOrderedByCreateDateDescCached(Long userId);
    Note findByIdAndUserId(Long noteId, Long userId);
}
