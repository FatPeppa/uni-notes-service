package org.skyhigh.notesservice.service.note;

import lombok.RequiredArgsConstructor;
import org.skyhigh.notesservice.model.entity.MediaMetadata;
import org.skyhigh.notesservice.model.entity.Note;
import org.skyhigh.notesservice.repository.MediaMetadataRepository;
import org.skyhigh.notesservice.repository.NoteRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotesCachedServiceImpl implements NotesCachedService {
    private final NoteRepository noteRepository;
    private final MediaMetadataRepository mediaMetadataRepository;

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

    @Override
    @Cacheable(value = "MediaMetadata", unless = "#result == null")
    public List<MediaMetadata> getMediaMetadataByNoteIdAndUserIdOrderedByCreateDateDesc(Long noteId, Long userId) {
        return mediaMetadataRepository.getMediaMetadataByNoteIdAndUserIdOrderByCreatedDateDesc(noteId, userId);
    }
}
