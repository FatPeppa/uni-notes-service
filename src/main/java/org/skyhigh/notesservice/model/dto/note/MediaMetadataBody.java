package org.skyhigh.notesservice.model.dto.note;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.skyhigh.notesservice.model.entity.MediaMetadata;
import org.skyhigh.notesservice.model.entity.MediaTypeEnum;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Тело метаданных файла с фото заметки")
public class MediaMetadataBody implements Comparable<MediaMetadataBody> {
    @Schema(description = "ID файла")
    private UUID mediaId;

    @Schema(description = "ID пользователя")
    private Long userId;

    @Schema(description = "Название файла")
    private String name;

    @Schema(description = "Тип файла")
    private MediaFileType mediaFileType;

    @Schema(description = "Время добавления файла в систему")
    private ZonedDateTime createdDate;

    public static MediaMetadataBody mapFromMediaMetadata(MediaMetadata mediaMetadata) {
        return new MediaMetadataBody(
                mediaMetadata.getId(),
                mediaMetadata.getUserId(),
                mediaMetadata.getName(),
                mapMediaTypeEnumToMediaFileType(mediaMetadata.getFileType()),
                mediaMetadata.getCreatedDate()
        );
    }

    public static MediaFileType mapMediaTypeEnumToMediaFileType(MediaTypeEnum mediaTypeEnum) {
        switch (mediaTypeEnum) {
            case NOTE_TXT -> {return MediaFileType.TXT;}
            case PNG -> {return MediaFileType.PNG;}
            case JPEG -> {return MediaFileType.JPEG;}
            case HEIC -> {return MediaFileType.HEIC;}
            default -> {throw new RuntimeException("Unexpected error occurred");}
        }
    }

    @Override
    public int compareTo(MediaMetadataBody o) {
        if (this.createdDate.isBefore(o.createdDate))
            return -1;
        else if (this.createdDate.isAfter(o.createdDate))
            return 1;
        else
            return 0;
    }
}
