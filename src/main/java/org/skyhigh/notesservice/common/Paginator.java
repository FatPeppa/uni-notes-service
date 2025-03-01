package org.skyhigh.notesservice.common;

import java.util.ArrayList;
import java.util.List;

public class Paginator {
    public static <T> List<T> paginate(List<T> originalList, int pageNumber, int pageSize) {
        int startIndex = (pageNumber - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, originalList.size());

        if (startIndex >= endIndex || startIndex < 0) {
            return new ArrayList<>();
        }

        return originalList.subList(startIndex, endIndex);
    }
}
