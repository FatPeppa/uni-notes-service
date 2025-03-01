package org.skyhigh.notesservice.service.note;

import lombok.RequiredArgsConstructor;
import org.skyhigh.notesservice.data.entity.Note;
import org.skyhigh.notesservice.repository.NoteRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotesCachedServiceImpl implements NotesCachedService {
    private final NoteRepository noteRepository;

    @Override
    @Cacheable(value = "NoteCache", unless = "#result == null")
    public List<Note> getNotesByUserIdOrderedByCreateDateDescCached(Long userId) {
        return noteRepository.findAllByUserIdOrderByCreatedDateDesc(userId);
    }

    @Override
    @Cacheable(value = "NoteByNoteIdAndUserIdCache", unless = "#result == null")
    public Note findByIdAndUserId(Long noteId, Long userId) {
        return noteRepository.findByIdAndUserId(noteId, userId);
    }
}
