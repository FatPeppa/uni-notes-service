package org.skyhigh.notesservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourcesConfiguration {
    @Value("${resources.max-note-image-file-size}")
    private Integer maxNoteImageFileSize;

    @Value("${resources.max-note-text-file-size}")
    private Integer maxNoteTextFileSize;

    @Value("${resources.max-note-images-amount}")
    private Integer maxNoteImagesAmount;

    @Value("${resources.max-users-categories-amount}")
    private Integer maxUsersCategoriesAmount;

    @Value("${resources.max-note-tag-ids-filter-for-search-amount}")
    private Integer maxNoteTagIdsFilterForSearchAmount;

    @Bean("MaxNoteImageFileSize")
    public Integer getMaxNoteImageFileSize() {
        return maxNoteImageFileSize;
    }

    @Bean("MaxNoteTextFileSize")
    public Integer getMaxNoteTextFileSize() {
        return maxNoteTextFileSize;
    }

    @Bean("MaxNoteImagesAmount")
    public Integer getMaxNoteImagesAmount() {
        return maxNoteImagesAmount;
    }

    @Bean("MaxUsersCategoriesAmount")
    public Integer getMaxUsersCategoriesAmount() {
        return maxUsersCategoriesAmount;
    }

    @Bean("MaxNoteTagIdsFilterForSearchAmount")
    public Integer getMaxNoteTagIdsFilterForSearchAmount() {return maxNoteTagIdsFilterForSearchAmount;}
}


