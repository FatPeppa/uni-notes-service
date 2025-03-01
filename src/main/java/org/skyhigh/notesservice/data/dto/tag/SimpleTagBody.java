package org.skyhigh.notesservice.data.dto.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Облегченное тело тега заметки")
public class SimpleTagBody {
    @Schema(description = "ID тега")
    private Long id;

    @Schema(description = "Имя тега")
    private String name;
}
