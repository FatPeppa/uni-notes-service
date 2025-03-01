package org.skyhigh.notesservice.data.dto.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Тело ответа по получению тегов")
public class GetTagsResponse {
    @Schema(description = "Список тегов")
    private List<FullTagBody> tags;
}
