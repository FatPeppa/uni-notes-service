package org.skyhigh.notesservice.model.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.shyhigh.grpc.notes.ResponseResultCode;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoteSearchResponseObject {
    private ResponseResultCode responseResultCode;
    private List<NoteObject> noteObjects;
}
