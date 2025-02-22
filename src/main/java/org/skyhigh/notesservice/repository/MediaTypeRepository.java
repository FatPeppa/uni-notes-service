package org.skyhigh.notesservice.repository;

import org.skyhigh.notesservice.data.entity.MediaType;
import org.skyhigh.notesservice.data.entity.MediaTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaTypeRepository extends JpaRepository<MediaType, MediaTypeEnum> {
}
