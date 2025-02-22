package org.skyhigh.notesservice.repository;

import org.skyhigh.notesservice.data.entity.MediaMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MediaMetadataRepository extends JpaRepository<MediaMetadata, UUID> {
}
