package org.skyhigh.notesservice.model.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoteObject implements Comparable<NoteObject> {
    private Long noteId;
    private Long userId;
    private String name;
    private NoteCategoryObject noteCategoryObject;
    private List<NoteTagObject> noteTagObjects;
    private String textExtraction;
    private UUID mediaId;
    private List<UUID> imageIds;
    private ZonedDateTime createdDate;
    private ZonedDateTime lastChangeDate;
    private byte[] content;

    @Override
    public int compareTo(NoteObject o) {
        if (this.createdDate.isBefore(o.createdDate))
            return -1;
        else if (this.createdDate.isAfter(o.createdDate))
            return 1;
        else
            return 0;
    }
}
