package org.skyhigh.notesservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class S3ClientConfiguration {
    @Value("${s3.bucket}")
    private String bucket;

    @Value("${s3.endpoint-url}")
    private String endpointUrl;

    @Value("${s3.key-id}")
    private String keyId;

    @Value("${s3.key}")
    private String key;

    @Value("${s3.region-name}")
    private String regionName;

    @Bean("S3Bucket")
    public String s3Bucket() {
        return bucket;
    }

    @Bean("S3EndpointUrl")
    public String s3EndpointUrl() {
        return endpointUrl;
    }

    @Bean("S3KeyId")
    public String s3KeyId() {
        return keyId;
    }

    @Bean("S3Key")
    public String s3Key() {
        return key;
    }

    @Bean("S3RegionName")
    public String s3RegionName() {
        return regionName;
    }

    @Bean("S3Client")
    public S3Client s3Client() {
        AwsCredentials credentials = AwsBasicCredentials.create(keyId, key);

        return S3Client.builder()
                .httpClient(ApacheHttpClient.create())
                .region(Region.of(regionName))
                .endpointOverride(URI.create(endpointUrl))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
