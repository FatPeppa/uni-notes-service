package org.skyhigh.notesservice.validation.exception;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MultipleFlkException extends RuntimeException {
    private List<FlkException> flkExceptions = new ArrayList<>();

    public void addAllExceptions(@NonNull List<FlkException> flkExceptions) {
        this.flkExceptions.addAll(flkExceptions);
    }

    public void addFlkException(final FlkException exception) {
        flkExceptions.add(exception);
    }
}
