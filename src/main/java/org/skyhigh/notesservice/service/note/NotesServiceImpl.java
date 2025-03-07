package org.skyhigh.notesservice.service.note;

import org.skyhigh.notesservice.common.Paginator;
import org.skyhigh.notesservice.model.dto.common.SortDirection;
import org.skyhigh.notesservice.model.dto.note.*;
import org.skyhigh.notesservice.model.dto.tag.SimpleTagBody;
import org.skyhigh.notesservice.model.entity.MediaMetadata;
import org.skyhigh.notesservice.model.entity.MediaTypeEnum;
import org.skyhigh.notesservice.model.entity.Note;
import org.skyhigh.notesservice.model.entity.NoteMedia;
import org.skyhigh.notesservice.repository.*;
import org.skyhigh.notesservice.service.resource.ResourceService;
import org.skyhigh.notesservice.service.resource.SizeCheckableFileType;
import org.skyhigh.notesservice.service.user.UserService;
import org.skyhigh.notesservice.validation.exception.FlkException;
import org.skyhigh.notesservice.validation.exception.MultipleFlkException;
import org.skyhigh.notesservice.validation.flk.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class NotesServiceImpl implements NotesService {
    private final ResourceService resourceService;
    private final UserService userService;
    private final NotesCachedService notesCachedService;

    private final NoteRepository noteRepository;
    private final MediaMetadataRepository mediaMetadataRepository;
    private final NoteMediaRepository noteMediaRepository;
    private final MediaTypeRepository mediaTypeRepository;
    private final CategoryRepository categoryRepository;
    private final UserPropertiesRepository userPropertiesRepository;
    private final NoteTagRepository noteTagRepository;
    private final TagRepository tagRepository;


    private final Integer maxNoteImagesAmount;

    public NotesServiceImpl(
            ResourceService resourceService,
            UserService userService,
            NotesCachedService notesCachedService,
            NoteRepository noteRepository,
            MediaMetadataRepository mediaMetadataRepository,
            NoteMediaRepository noteMediaRepository,
            MediaTypeRepository mediaTypeRepository,
            CategoryRepository categoryRepository,
            UserPropertiesRepository userPropertiesRepository,
            NoteTagRepository noteTagRepository,
            TagRepository tagRepository,
            @Qualifier("MaxNoteImagesAmount") Integer maxNoteImagesAmount
    ) {
        this.resourceService = resourceService;
        this.userService = userService;
        this.notesCachedService = notesCachedService;
        this.noteRepository = noteRepository;
        this.mediaMetadataRepository = mediaMetadataRepository;
        this.noteMediaRepository = noteMediaRepository;
        this.mediaTypeRepository = mediaTypeRepository;
        this.categoryRepository = categoryRepository;
        this.userPropertiesRepository = userPropertiesRepository;
        this.noteTagRepository = noteTagRepository;
        this.tagRepository = tagRepository;
        this.maxNoteImagesAmount = maxNoteImagesAmount;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"NoteCache", "NoteByNoteIdAndUserIdCache"}, allEntries = true)
    public CreateNoteResponse createNote(CreateNoteRequest createNoteRequest) throws IOException {
        List<FlkException> flkExceptions = new ArrayList<>();
        var userId = userService.getCurrentUser().getId();

        //1. Проверить наличие заметки с указанным именем
        if (noteRepository.findByUserIdAndName(userId, createNoteRequest.getName()) != null)
            flkExceptions.add(FlkException.builder()
                    .flkCode(Flk10000011.getCode())
                    .flkMessage(Flk10000011.getMessage())
                    .flkParameterName(Flk10000011.getFieldName())
                    .build());

        //2. Проверить существование категории если заполнена
        if (createNoteRequest != null
                && createNoteRequest.getCategoryId() != null
                && categoryRepository.findByIdAndUserId(createNoteRequest.getCategoryId(), userId) == null)
            flkExceptions.add(FlkException.builder()
                    .flkCode(Flk10000009.getCode())
                    .flkMessage(Flk10000009.getMessage())
                    .flkParameterName(Flk10000009.getFieldName())
                    .build());

        if (!flkExceptions.isEmpty())
            throw new MultipleFlkException(flkExceptions);

        var createdDate = ZonedDateTime.now();

        //3. Создать заметку
        var note = Note.builder()
                .id(null)
                .userId(userId)
                .categoryId(createNoteRequest.getCategoryId())
                .name(createNoteRequest.getName())
                .createdDate(createdDate)
                .lastChangeDate(ZonedDateTime.now())
                .build();

        note = noteRepository.save(note);

        return CreateNoteResponse.builder()
                .noteId(note.getId())
                .name(note.getName())
                .createdDate(createdDate)
                .build();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"NoteCache", "NoteByNoteIdAndUserIdCache", "MediaMetadata"}, allEntries = true)
    public UploadNoteImageResponse uploadNoteImage(Long noteId, MultipartFile image) throws IOException {
        List<FlkException> flkExceptions = new ArrayList<>();
        var userId = userService.getCurrentUser().getId();

        //1. Проверить наличие заметки по id заметки и id пользователя
        Note note = notesCachedService.findByIdAndUserId(noteId, userId);
        if (note == null)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000004.getCode())
                    .flkMessage(Flk10000004.getMessage())
                    .flkParameterName(Flk10000004.getFieldName())
                    .build()));

        if (image == null || image.isEmpty())
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000023.getCode())
                    .flkMessage(Flk10000023.getMessage())
                    .build()));

        //2. Проверить тип файла на допустимость в Системе и на то что это фото
        MediaTypeEnum fileType = resourceService.getFileType(image);
        if (fileType == MediaTypeEnum.NOT_SUPPORTED)
            flkExceptions.add(FlkException.builder()
                    .flkCode(Flk10000005.getCode())
                    .flkMessage(Flk10000005.getMessage())
                    .flkParameterName(Flk10000005.getFieldName())
                    .build());
        else if (fileType == MediaTypeEnum.NOTE_TXT)
            flkExceptions.add(FlkException.builder()
                    .flkCode(Flk10000006.getCode())
                    .flkMessage(Flk10000006.getMessage())
                    .flkParameterName(Flk10000006.getFieldName())
                    .build());

        //3. Проверить размер файла
        if (flkExceptions.stream().noneMatch(x -> x.getCode().equals("Flk10000005")
                        || x.getCode().equals("Flk10000006")))
            if (resourceService.checkFileSizeLimited(image, SizeCheckableFileType.NOTE_IMAGE))
                flkExceptions.add(FlkException.builder()
                        .flkCode(Flk10000007.getCode())
                        .flkMessage(Flk10000007.getMessage())
                        .flkParameterName(Flk10000007.getFieldName())
                        .build());

        //4. Проверить количество фото у заметки
        Integer noteImagesAmount = noteMediaRepository.countByNoteId(noteId);
        if (noteImagesAmount > maxNoteImagesAmount)
            flkExceptions.add(FlkException.builder()
                    .flkCode(Flk10000008.getCode())
                    .flkMessage(Flk10000008.getMessage())
                    .build()
            );

        if (!flkExceptions.isEmpty())
            throw new MultipleFlkException(flkExceptions);

        //5. Создать метаданные
        var mediaMetadata = MediaMetadata.builder()
                .id(null)
                .userId(userId)
                .name(image.getOriginalFilename())
                .fileType(fileType)
                .createdDate(ZonedDateTime.now())
                .url(String.format("users/%s/resources/images", userId.toString()))
                .build();

        mediaMetadata = mediaMetadataRepository.save(mediaMetadata);

        //6. Создать связку метаданных и заметки
        noteMediaRepository.saveEntity(noteId, mediaMetadata.getId(),ZonedDateTime.now());

        //7. Сохранить файл
        String key = resourceService.uploadImage(mediaMetadata.getId().toString(), image);

        //8. Обновить дату обновления заметки
        var lastChangeDate = ZonedDateTime.now();

        noteRepository.updateLastChangeDateById(
                noteId,
                lastChangeDate
        );

        return UploadNoteImageResponse.builder()
                .mediaId(mediaMetadata.getId())
                .noteId(noteId)
                .lastChangeDate(lastChangeDate)
                .build();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"NoteCache", "NoteByNoteIdAndUserIdCache"}, allEntries = true)
    public UploadNoteTextResponse uploadNoteText(Long noteId, MultipartFile text) throws IOException {
        List<FlkException> flkExceptions = new ArrayList<>();
        var userId = userService.getCurrentUser().getId();

        //1. Проверить наличие заметки по id заметки и id пользователя
        Note note = notesCachedService.findByIdAndUserId(noteId, userId);
        if (note == null)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000004.getCode())
                    .flkMessage(Flk10000004.getMessage())
                    .flkParameterName(Flk10000004.getFieldName())
                    .build()));

        if (text == null || text.isEmpty())
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000023.getCode())
                    .flkMessage(Flk10000023.getMessage())
                    .build()));

        //2. Проверить тип файла на допустимость в Системе и на то что это текст
        MediaTypeEnum fileType = resourceService.getFileType(text);
        if (fileType == MediaTypeEnum.NOT_SUPPORTED)
            flkExceptions.add(FlkException.builder()
                    .flkCode(Flk10000005.getCode())
                    .flkMessage(Flk10000005.getMessage())
                    .flkParameterName(Flk10000005.getFieldName())
                    .build());
        else if (fileType != MediaTypeEnum.NOTE_TXT)
            flkExceptions.add(FlkException.builder()
                    .flkCode(Flk10000010.getCode())
                    .flkMessage(Flk10000010.getMessage())
                    .flkParameterName(Flk10000010.getFieldName())
                    .build());

        //3. Проверить размер файла
        if (flkExceptions.stream().noneMatch(x -> x.getCode().equals("Flk10000005")
                || x.getCode().equals("Flk10000010")))
            if (resourceService.checkFileSizeLimited(text, SizeCheckableFileType.NOTE_TXT))
                flkExceptions.add(FlkException.builder()
                        .flkCode(Flk10000007.getCode())
                        .flkMessage(Flk10000007.getMessage())
                        .flkParameterName(Flk10000007.getFieldName())
                        .build());

        //4. Проверить наличие текстового файла у заметки
        if (note.getMediaId() != null)
            flkExceptions.add(FlkException.builder()
                    .flkCode(Flk10000012.getCode())
                    .flkMessage(Flk10000012.getMessage())
                    .build()
            );

        if (!flkExceptions.isEmpty())
            throw new MultipleFlkException(flkExceptions);

        //5. Создать метаданные
        var mediaMetadata = MediaMetadata.builder()
                .id(null)
                .userId(userId)
                .name(text.getOriginalFilename())
                .fileType(fileType)
                .createdDate(ZonedDateTime.now())
                .url(String.format("users/%s/resources/text", userId.toString()))
                .build();

        mediaMetadata = mediaMetadataRepository.save(mediaMetadata);

        //6. Распарсить текст файла
        String noteTextExtraction = resourceService.parseTextFile(text, 512);

        //7. Добавить Id файла и выжимку его текста к заметке
        var lastChangeDate = ZonedDateTime.now();

        noteRepository.updateMediaIdAndTextExtractionAndLastChangeDateById(
                noteId,
                mediaMetadata.getId(),
                noteTextExtraction,
                lastChangeDate
        );

        //8. Сохранить файл
        String key = resourceService.uploadTextFile(mediaMetadata.getId().toString(), text);

        return UploadNoteTextResponse.builder()
                .mediaId(mediaMetadata.getId())
                .noteId(noteId)
                .lastChangeDate(lastChangeDate)
                .build();
    }

    @Override
    public GetNotesResponse getNotes(
            Long noteId,
            String noteName,
            Long categoryId,
            List<Long> tagIds,
            ZonedDateTime beginDate,
            ZonedDateTime endDate,
            boolean extended,
            boolean showImages,
            SortDirection createdDateSortDirection,
            Integer limit,
            Integer offset
    ) {
        if (limit <= 0 || offset <= 0)
            return GetNotesResponse.builder()
                    .notes(new ArrayList<>())
                    .build();

        if (beginDate != null && endDate != null && endDate.isBefore(beginDate))
            return GetNotesResponse.builder()
                    .notes(new ArrayList<>())
                    .build();

        if (tagIds != null && !tagIds.isEmpty() && tagIds.size() > 10)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000024.getCode())
                    .flkMessage(Flk10000024.getMessage())
                    .build()));

        var userId = userService.getCurrentUser().getId();
        var userProperties = userPropertiesRepository.findByUserId(userId);

        //1. Обновление предпочитаемой сортировки в параметрах пользователя в случае необходимости
        if (createdDateSortDirection != null
                && createdDateSortDirection != userProperties.getLastNotesCreatedDateSortDirection()) {
            userProperties.setLastNotesCreatedDateSortDirection(createdDateSortDirection);
            userPropertiesRepository.updateLastNotesCreatedDateSortDirectionByUserId(userId, createdDateSortDirection.name());
        }

        //2. Поиск заметок с сортировкой по дате в обратном порядке (от настоящего к прошлому)
        var notes = notesCachedService.getNotesByUserIdOrderedByCreateDateDescCached(userId);

        if (notes == null)
            return GetNotesResponse.builder()
                    .notes(new ArrayList<>())
                    .build();

        List<NoteContent> noteContents = null;

        //3. Преобразование списка заметок к расширенному/упрощенному представлению и фильтрация
        if (extended) {
            //3.1 Маппинг к ExtendedNoteBody
            List<NoteContent> finalNoteContents = new ArrayList<>();
            notes.forEach(x -> {
                var tags = tagRepository.findByNoteId(x.getId());
                var imageIds = noteMediaRepository.findByNoteId(x.getId());

                finalNoteContents.add(new ExtendedNoteBody(
                        x.getId(),
                        userId,
                        x.getCategoryId(),
                        x.getCategoryId() != null
                                ? categoryRepository.findByIdAndUserId(x.getCategoryId(), userId).getName()
                                : null,
                        x.getName(),
                        x.getTextExtraction(),
                        tags == null ? null : tags.stream().map(y -> new SimpleTagBody(y.getId(), y.getName())).toList(),
                        x.getMediaId(),
                        imageIds == null || !showImages ? null : imageIds.stream().map(NoteMedia::getMediaId).toList(),
                        x.getCreatedDate(),
                        x.getLastChangeDate()));
            });

            //3.2 Фильтрация
            noteContents = new ArrayList<>(finalNoteContents).stream()
                    .filter(x -> {if (noteId != null) return ((ExtendedNoteBody) x).getNoteId().equals(noteId); else return true;})
                    .filter(x -> {if (noteName != null && noteName.isBlank()) return ((ExtendedNoteBody) x).getName().contains(noteName); else return true;})
                    .filter(x -> {if (categoryId != null) return ((ExtendedNoteBody) x).getCategoryId().equals(categoryId); else return true;})
                    .filter(x -> {
                        if (tagIds != null && !tagIds.isEmpty()) {
                            return tagIds.stream().allMatch(y -> noteTagRepository
                                    .findByNoteIdAndTagId(((ExtendedNoteBody) x).getNoteId(), y) != null);
                        }
                        else return true;
                    })
                    .filter(x -> {if (beginDate != null) return x.getCreatedDate().isAfter(beginDate) || x.getCreatedDate().isEqual(beginDate); else return true;})
                    .filter(x -> {if (endDate != null) return x.getCreatedDate().isBefore(endDate) || x.getCreatedDate().isEqual(endDate); else return true;})
                    .toList();
        } else {
            //3.1 Маппинг к NoteBody
            List<NoteContent> finalNoteContents = new ArrayList<>();

            notes.forEach(x -> {
                var imageIds = noteMediaRepository.findByNoteId(x.getId());

                finalNoteContents.add(new NoteBody(
                        x.getId(),
                        userId,
                        x.getCategoryId(),
                        x.getName(),
                        x.getTextExtraction(),
                        x.getMediaId(),
                        imageIds == null || !showImages ? null : imageIds.stream().map(NoteMedia::getMediaId).toList(),
                        x.getCreatedDate(),
                        x.getLastChangeDate()));
            });

            //3.2 Фильтрация
            noteContents = new ArrayList<>(finalNoteContents).stream()
                    .filter(x -> {if (noteId != null) return ((NoteBody) x).getNoteId().equals(noteId); else return true;})
                    .filter(x -> {if (noteName != null && !noteName.isBlank()) return ((NoteBody) x).getName().contains(noteName);else return true;})
                    .filter(x -> {if (categoryId != null) return ((NoteBody) x).getCategoryId().equals(categoryId); else return true;})
                    .filter(x -> {
                        if (tagIds != null && !tagIds.isEmpty()) {
                            return tagIds.stream().allMatch(y -> noteTagRepository
                                    .findByNoteIdAndTagId(((NoteBody) x).getNoteId(), y) != null);
                        }
                        else return true;
                    })
                    .filter(x -> {if (beginDate != null) return x.getCreatedDate().isAfter(beginDate) || x.getCreatedDate().isEqual(beginDate); else return true;})
                    .filter(x -> {if (endDate != null) return x.getCreatedDate().isBefore(endDate) || x.getCreatedDate().isEqual(endDate); else return true;})
                    .toList();
        }

        //4. Сортировка в прямом порядке в случае необходимости
        if (userProperties.getLastNotesCreatedDateSortDirection() == SortDirection.ASC)
            noteContents = noteContents.stream().sorted().toList();

        //5. Применение параметров пагинации
        noteContents = Paginator.paginate(
                noteContents,
                offset,
                limit
        );

        return GetNotesResponse.builder()
                .notes(noteContents)
                .build();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"NoteCache", "NoteByNoteIdAndUserIdCache"}, allEntries = true)
    public void updateNoteBody(Long noteId, UpdateNoteBodyRequest updateNoteBodyRequest) {
        List<FlkException> flkExceptions = new ArrayList<>();
        var userId = userService.getCurrentUser().getId();

        //1. Проверить наличие заметки у юзера
        Note note = notesCachedService.findByIdAndUserId(noteId, userId);
        if (note == null)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000004.getCode())
                    .flkMessage(Flk10000004.getMessage())
                    .flkParameterName(Flk10000004.getFieldName())
                    .build()));

        //2. Проверить уникальность заголовка в заметке
        var noteWithSameName = noteRepository.findByUserIdAndName(userId, updateNoteBodyRequest.getName());
        if (noteWithSameName != null && !noteWithSameName.getId().equals(noteId))
            flkExceptions.add(FlkException.builder()
                    .flkCode(Flk10000011.getCode())
                    .flkMessage(Flk10000011.getMessage())
                    .flkParameterName(Flk10000011.getFieldName())
                    .build());

        //3. Проверить существование категории по ID
        if (updateNoteBodyRequest.getCategoryId() != null
                && categoryRepository.findByIdAndUserId(updateNoteBodyRequest.getCategoryId(), userId) == null)
            flkExceptions.add(FlkException.builder()
                    .flkCode(Flk10000009.getCode())
                    .flkMessage(Flk10000009.getMessage())
                    .flkParameterName(Flk10000009.getFieldName())
                    .build());

        if (!flkExceptions.isEmpty())
            throw new MultipleFlkException(flkExceptions);

        noteRepository.updateCategoryIdAndNameAndLastChangeDateById(
                noteId,
                updateNoteBodyRequest.getCategoryId(),
                updateNoteBodyRequest.getName(),
                ZonedDateTime.now()
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = {"NoteCache", "NoteByNoteIdAndUserIdCache"}, allEntries = true)
    public UpdateNoteTextResponse updateNoteText(Long noteId, MultipartFile text) throws IOException {
        List<FlkException> flkExceptions = new ArrayList<>();
        var userId = userService.getCurrentUser().getId();

        //1. Проверить наличие заметки по id заметки и id пользователя
        Note note = notesCachedService.findByIdAndUserId(noteId, userId);

        if (note == null)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000004.getCode())
                    .flkMessage(Flk10000004.getMessage())
                    .flkParameterName(Flk10000004.getFieldName())
                    .build()));

        if (text == null || text.isEmpty())
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000023.getCode())
                    .flkMessage(Flk10000023.getMessage())
                    .build()));

        //2. Проверить тип файла на допустимость в Системе и на то что это текст
        MediaTypeEnum fileType = resourceService.getFileType(text);
        if (fileType == MediaTypeEnum.NOT_SUPPORTED)
            flkExceptions.add(FlkException.builder()
                    .flkCode(Flk10000005.getCode())
                    .flkMessage(Flk10000005.getMessage())
                    .flkParameterName(Flk10000005.getFieldName())
                    .build());
        else if (fileType != MediaTypeEnum.NOTE_TXT)
            flkExceptions.add(FlkException.builder()
                    .flkCode(Flk10000010.getCode())
                    .flkMessage(Flk10000010.getMessage())
                    .flkParameterName(Flk10000010.getFieldName())
                    .build());

        //4. Проверить размер файла
        if (flkExceptions.stream().noneMatch(x -> x.getCode().equals("Flk10000005")
                || x.getCode().equals("Flk10000010")))
            if (resourceService.checkFileSizeLimited(text, SizeCheckableFileType.NOTE_TXT))
                flkExceptions.add(FlkException.builder()
                        .flkCode(Flk10000007.getCode())
                        .flkMessage(Flk10000007.getMessage())
                        .flkParameterName(Flk10000007.getFieldName())
                        .build());

        //5. Проверить наличие текстового файла у заметки
        if (note.getMediaId() == null)
            flkExceptions.add(FlkException.builder()
                    .flkCode(Flk10000014.getCode())
                    .flkMessage(Flk10000014.getMessage())
                    .build()
            );

        if (!flkExceptions.isEmpty())
            throw new MultipleFlkException(flkExceptions);

        //6. Обновить метаданные файла
        mediaMetadataRepository.updateNameAndCreatedDateById(
                note.getMediaId(),
                text.getOriginalFilename(),
                ZonedDateTime.now()
        );

        //7. Удалить файл по Id
        resourceService.deleteTextFile(note.getMediaId().toString());

        //8. Сохранить файл
        String key = resourceService.uploadTextFile(note.getMediaId().toString(), text);

        //9. Обновить дату обновления в заметке
        var lastChangeDate = ZonedDateTime.now();
        noteRepository.updateLastChangeDateById(noteId, lastChangeDate);

        return UpdateNoteTextResponse.builder()
                .mediaId(note.getMediaId())
                .noteId(noteId)
                .lastChangeDate(lastChangeDate)
                .build();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"NoteCache", "NoteByNoteIdAndUserIdCache", "MediaMetadata"}, allEntries = true)
    public void deleteNote(Long noteId, boolean deleteCascade) throws IOException {
        var userId = userService.getCurrentUser().getId();

        //1. Проверить наличие заметки по id заметки и id пользователя
        Note note = notesCachedService.findByIdAndUserId(noteId, userId);
        if (note == null)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000004.getCode())
                    .flkMessage(Flk10000004.getMessage())
                    .flkParameterName(Flk10000004.getFieldName())
                    .build()));

        //2. Удалить все фото заметки в случае указания признака каскадного удаления
        if (deleteCascade) {
            var noteMediaList = noteMediaRepository.findByNoteId(noteId);
            noteMediaRepository.deleteByNoteId(noteId);
            for (var noteMedia : noteMediaList) {
                resourceService.deleteImage(noteMedia.getMediaId().toString());
                mediaMetadataRepository.deleteById(noteMedia.getMediaId());
            }
        } else if (noteMediaRepository.countByNoteId(noteId) > 0)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000015.getCode())
                    .flkMessage(Flk10000015.getMessage())
                    .flkParameterName(Flk10000015.getFieldName())
                    .build()));

        //3. Удалить текстовый файл заметки
        if (note.getMediaId() != null) {
            resourceService.deleteTextFile(note.getMediaId().toString());
            mediaMetadataRepository.deleteById(note.getMediaId());
        }

        //4. Удалить заметку
        noteRepository.deleteById(noteId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"NoteCache", "NoteByNoteIdAndUserIdCache", "MediaMetadata"}, allEntries = true)
    public void deleteImage(Long noteId, UUID mediaId) throws IOException {
        var userId = userService.getCurrentUser().getId();

        //1. Проверить наличие заметки по id заметки и id пользователя
        Note note = notesCachedService.findByIdAndUserId(noteId, userId);
        if (note == null)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000004.getCode())
                    .flkMessage(Flk10000004.getMessage())
                    .flkParameterName(Flk10000004.getFieldName())
                    .build()));

        List<FlkException> flkExceptions = new ArrayList<>();

        //2. Проверить принадлежность фото заметке
        if (noteMediaRepository.findByNoteIdAndMediaId(noteId, mediaId) == null)
            flkExceptions.add(FlkException.builder()
                    .flkCode(Flk10000016.getCode())
                    .flkMessage(Flk10000016.getMessage())
                    .flkParameterName(Flk10000016.getFieldName())
                    .build());

        //3. Проверить существование фото
        if (!resourceService.isImageExists(mediaId.toString()))
            flkExceptions.add(FlkException.builder()
                    .flkCode(Flk10000016.getCode())
                    .flkMessage(Flk10000016.getMessage())
                    .flkParameterName(Flk10000016.getFieldName())
                    .build());

        if (!flkExceptions.isEmpty())
            throw new MultipleFlkException(flkExceptions);

        //4. Удаление фото, связки с заметкой и метаданных
        resourceService.deleteImage(mediaId.toString());
        noteMediaRepository.deleteByNoteIdAndMediaId(noteId, mediaId);
        mediaMetadataRepository.deleteById(mediaId);

        //5. Обновление даты изменения в заметке
        noteRepository.updateLastChangeDateById(noteId, ZonedDateTime.now());
    }

    @Override
    public byte[] getImage(Long noteId, UUID mediaId) throws IOException {
        var userId = userService.getCurrentUser().getId();

        //1. Проверить наличие заметки по id заметки и id пользователя
        Note note = notesCachedService.findByIdAndUserId(noteId, userId);
        if (note == null)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000004.getCode())
                    .flkMessage(Flk10000004.getMessage())
                    .flkParameterName(Flk10000004.getFieldName())
                    .build()));

        List<FlkException> flkExceptions = new ArrayList<>();

        //2. Проверить принадлежность фото заметке
        if (noteMediaRepository.findByNoteIdAndMediaId(noteId, mediaId) == null)
            flkExceptions.add(FlkException.builder()
                    .flkCode(Flk10000016.getCode())
                    .flkMessage(Flk10000016.getMessage())
                    .flkParameterName(Flk10000016.getFieldName())
                    .build());

        //3. Проверить существование фото
        if (!resourceService.isImageExists(mediaId.toString()))
            flkExceptions.add(FlkException.builder()
                    .flkCode(Flk10000016.getCode())
                    .flkMessage(Flk10000016.getMessage())
                    .flkParameterName(Flk10000016.getFieldName())
                    .build());

        if (!flkExceptions.isEmpty())
            throw new MultipleFlkException(flkExceptions);

        return resourceService.downloadImage(mediaId.toString());
    }

    @Override
    public byte[] getText(Long noteId) throws IOException {
        var userId = userService.getCurrentUser().getId();

        //1. Проверить наличие заметки по id заметки и id пользователя
        var note = notesCachedService.findByIdAndUserId(noteId, userId);
        if (note == null)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000004.getCode())
                    .flkMessage(Flk10000004.getMessage())
                    .flkParameterName(Flk10000004.getFieldName())
                    .build()));

        if (note.getMediaId() == null)
            return null;

        //2. Проверить существование текстового файла
        if (!resourceService.isTextFileExists(note.getMediaId().toString()))
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000022.getCode())
                    .flkMessage(Flk10000022.getMessage())
                    .build()));

        return resourceService.downloadTextFile(note.getMediaId().toString());
    }

    @Override
    public GetNoteMediaMetadataResponse getNoteMediaMetadata(
            Long noteId,
            List<UUID> mediaIds,
            ZonedDateTime beginDate,
            ZonedDateTime endDate,
            SortDirection createdDateSortDirection,
            Integer limit,
            Integer offset
    ) {
        if (limit <= 0 || offset <= 0)
            return GetNoteMediaMetadataResponse.builder()
                    .metadataBodyList(new ArrayList<>())
                    .build();

        if (beginDate != null && endDate != null && endDate.isBefore(beginDate))
            return GetNoteMediaMetadataResponse.builder()
                    .metadataBodyList(new ArrayList<>())
                    .build();

        var userId = userService.getCurrentUser().getId();

        //1. Поиск метаданных файлов по дате в обратном порядке (от настоящего к прошлому)
        var mediaMetadata = notesCachedService.getMediaMetadataByNoteIdAndUserIdOrderedByCreateDateDesc(noteId, userId);

        if (mediaMetadata == null)
            return GetNoteMediaMetadataResponse.builder()
                    .metadataBodyList(new ArrayList<>())
                    .build();

        //2. Преобразование списка метаданных файлов к dto и фильтрация
        List<MediaMetadataBody> mediaMetadataBodies = null;

        //2.1 Маппинг к MediaMetadataBody
        List<MediaMetadataBody> finalMediaMetadataBodies = new ArrayList<>();
        mediaMetadata.forEach(x -> finalMediaMetadataBodies
                .add(MediaMetadataBody.mapFromMediaMetadata(x)));

        //2.2 Фильтрация
        mediaMetadataBodies = new ArrayList<>(finalMediaMetadataBodies).stream()
                .filter(x -> {
                    if (mediaIds != null && !mediaIds.isEmpty()) {
                        return mediaIds.stream().anyMatch(y -> y.equals(x.getMediaId()));
                    }
                    else return true;
                })
                .filter(x -> {if (beginDate != null) return x.getCreatedDate().isAfter(beginDate) || x.getCreatedDate().isEqual(beginDate); else return true;})
                .filter(x -> {if (endDate != null) return x.getCreatedDate().isBefore(endDate) || x.getCreatedDate().isEqual(endDate); else return true;})
                .toList();

        //3. Сортировка в прямом порядке в случае необходимости
        if (createdDateSortDirection == SortDirection.ASC)
            mediaMetadataBodies = mediaMetadataBodies.stream().sorted().toList();

        mediaMetadataBodies = Paginator.paginate(
                mediaMetadataBodies,
                offset,
                limit
        );

        return GetNoteMediaMetadataResponse.builder()
                .metadataBodyList(mediaMetadataBodies)
                .build();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"NoteCache", "NoteByNoteIdAndUserIdCache"}, allEntries = true)
    public void updateNoteTags(Long noteId, UpdateNoteTagsRequest updateNoteTagsRequest) {
        var userId = userService.getCurrentUser().getId();

        //1. Проверить наличие заметки по id заметки и id пользователя
        Note note = notesCachedService.findByIdAndUserId(noteId, userId);
        if (note == null)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000004.getCode())
                    .flkMessage(Flk10000004.getMessage())
                    .flkParameterName(Flk10000004.getFieldName())
                    .build()));

        //2. Проверка существования тегов у юзера
        List<FlkException> flkExceptions = new ArrayList<>();
        if (updateNoteTagsRequest.getTagIds() != null)
            for (var tagId : updateNoteTagsRequest.getTagIds())
                if (tagRepository.findByTagIdAndUserId(tagId, userId) == null)
                    flkExceptions.add(FlkException.builder()
                            .flkCode(Flk10000017.getCode())
                            .flkMessage(Flk10000017.getMessage())
                            .flkParameterName(Flk10000017.getFieldName())
                            .build());

        if (!flkExceptions.isEmpty())
            throw new MultipleFlkException(flkExceptions);

        //3. Удалить текущие теги и добавить новые в случае необходимости
        noteTagRepository.deleteByNoteId(noteId);

        if (updateNoteTagsRequest.getTagIds() != null && !updateNoteTagsRequest.getTagIds().isEmpty())
            updateNoteTagsRequest.getTagIds().forEach(x -> noteTagRepository.saveEntity(x, noteId, ZonedDateTime.now()));

        //4. Обновить дату обновления в заметке
        noteRepository.updateLastChangeDateById(noteId, ZonedDateTime.now());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"NoteCache", "NoteByNoteIdAndUserIdCache"}, allEntries = true)
    public void updateNoteCategory(Long noteId, UpdateNoteCategoryRequest updateNoteCategoryRequest) {
        var userId = userService.getCurrentUser().getId();

        //1. Проверить наличие заметки по id заметки и id пользователя
        Note note = notesCachedService.findByIdAndUserId(noteId, userId);
        if (note == null)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000004.getCode())
                    .flkMessage(Flk10000004.getMessage())
                    .flkParameterName(Flk10000004.getFieldName())
                    .build()));

        //2. Проверить существование целевой категории у юзера если она заполнена
        if (updateNoteCategoryRequest.getCategoryId() != null
                && categoryRepository.findByIdAndUserId(updateNoteCategoryRequest.getCategoryId(), userId) == null)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000009.getCode())
                    .flkMessage(Flk10000009.getMessage())
                    .flkParameterName(Flk10000009.getFieldName())
                    .build()));

        //3. Обновить категорию заметки
        noteRepository.updateCategoryIdAndLastChangeDateById(noteId, updateNoteCategoryRequest.getCategoryId(), ZonedDateTime.now());
    }
}
