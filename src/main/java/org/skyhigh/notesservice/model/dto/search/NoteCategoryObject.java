package org.skyhigh.notesservice.model.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoteCategoryObject {
    private Long categoryId;
    private String categoryName;
}
