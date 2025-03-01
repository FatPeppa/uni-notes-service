package org.skyhigh.notesservice.data.dto.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Полное тело тега")
public class FullTagBody implements Comparable<FullTagBody> {
    @Schema(description = "ID тега")
    private Long tagId;

    @Schema(description = "ID пользователя")
    private Long userId;

    @Schema(description = "Имя тега")
    private String name;

    @Schema(description = "Дата и время создания тега")
    private ZonedDateTime createdDate;

    @Schema(description = "Дата и время обновления тега")
    private ZonedDateTime lastChangeDate;

    @Override
    public int compareTo(FullTagBody o) {
        if (this.createdDate.isBefore(o.createdDate))
            return -1;
        else if (this.createdDate.isAfter(o.createdDate))
            return 1;
        else
            return 0;
    }
}
