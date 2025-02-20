package org.skyhigh.notesservice.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.skyhigh.notesservice.validation.annotation.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestDTO {
    @NotEmpty
    private String test1;

    private String test2;

    private String test3;
}
