package org.skyhigh.notesservice.repository;

import java.io.IOException;

public interface S3ObjectRepository {
    void uploadS3Object(String key, String contentType, byte[] file) throws IOException;
    void deleteS3Object(String key) throws IOException;
    byte[] getS3Object(String key) throws IOException;
}
