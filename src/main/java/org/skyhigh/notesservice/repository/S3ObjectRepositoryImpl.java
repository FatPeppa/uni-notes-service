package org.skyhigh.notesservice.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Repository
public class S3ObjectRepositoryImpl implements S3ObjectRepository {
    private final S3Client s3Client;
    private final String bucketName;

    public S3ObjectRepositoryImpl(
            @Qualifier("S3Client") S3Client s3Client,
            @Qualifier("S3Bucket") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public void uploadS3Object(String key, String contentType, byte[] file) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file));
    }

    @Override
    public void deleteS3Object(String key) throws IOException {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    @Override
    public byte[] getS3Object(String key) throws IOException {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        var inputStream = s3Client.getObject(objectRequest);
        return inputStream.readAllBytes();
    }
}
