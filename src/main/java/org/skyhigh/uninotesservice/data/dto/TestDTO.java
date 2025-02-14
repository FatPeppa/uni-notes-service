package org.skyhigh.uninotesservice.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.skyhigh.uninotesservice.validation.annotation.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestDTO {
    @NotEmpty
    private String test1;

    private String test2;

    private String test3;
}
