package org.skyhigh.notesservice.service.resource;

import org.skyhigh.notesservice.model.entity.MediaTypeEnum;
import org.skyhigh.notesservice.repository.S3ObjectRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

@Service
public class ResourceServiceImpl implements ResourceService {
    private final S3ObjectRepository s3ObjectRepository;

    private static final String imageKeyPrefix = "image/";
    private static final String textKeyPrefix = "text/";

    private final Integer maxNoteImageFileSize;
    private final Integer maxNoteTextFileSize;

    public ResourceServiceImpl (
            S3ObjectRepository s3ObjectRepository,
            @Qualifier("MaxNoteImageFileSize") Integer maxNoteImageFileSize,
            @Qualifier("MaxNoteTextFileSize") Integer maxNoteTextFileSize
    ) {
        this.s3ObjectRepository = s3ObjectRepository;
        this.maxNoteImageFileSize = maxNoteImageFileSize;
        this.maxNoteTextFileSize = maxNoteTextFileSize;
    }

    @Override
    public String uploadImage(String key, MultipartFile image) throws IOException {
        var fullKey = imageKeyPrefix + key;
        s3ObjectRepository.uploadS3Object(fullKey, image.getContentType(), image.getBytes());
        return key;
    }

    @Override
    public String uploadTextFile(String key, MultipartFile text) throws IOException {
        var fullKey = textKeyPrefix + key;
        s3ObjectRepository.uploadS3Object(fullKey, text.getContentType(), text.getBytes());
        return key;
    }

    @Override
    public boolean isFileSupported(MultipartFile file) throws IOException {
        return Arrays.stream(MediaTypeEnum.values()).anyMatch(x -> x != MediaTypeEnum.NOT_SUPPORTED
                && Arrays.stream(x.getFileTypes()).allMatch(y -> y.equals(file.getContentType())));
    }

    @Override
    public MediaTypeEnum getFileType(MultipartFile file) throws IOException {
        return switch (file.getContentType()) {
            case "image/jpeg" -> MediaTypeEnum.JPEG;
            case "image/png" -> MediaTypeEnum.PNG;
            case "image/heic" -> MediaTypeEnum.HEIC;
            case "text/plain" -> MediaTypeEnum.NOTE_TXT;
            default -> MediaTypeEnum.NOT_SUPPORTED;
        };
    }

    @Override
    public boolean isFileTypeSupported(MediaTypeEnum file) throws IOException {
        return file != MediaTypeEnum.NOT_SUPPORTED;
    }

    @Override
    public boolean checkFileSizeLimited(MultipartFile file, SizeCheckableFileType sizeCheckableFileType) throws IOException {
        return switch (sizeCheckableFileType) {
            case NOTE_IMAGE -> {
                var maxNoteImageFileSizeInBytes = maxNoteImageFileSize.longValue() * 1024L * 1024L;
                yield file.getSize() > maxNoteImageFileSizeInBytes;
            }
            case NOTE_TXT -> {
                var maxNoteTextFileSizeInBytes = maxNoteTextFileSize.longValue() * 1024L * 1024L;
                yield file.getSize() > maxNoteTextFileSizeInBytes;
            }
            default -> true;
        };
    }

    @Override
    public byte[] downloadImage(String key) throws IOException {
        var fullKey = imageKeyPrefix + key;
        return s3ObjectRepository.getS3Object(fullKey);
    }

    @Override
    public byte[] downloadTextFile(String key) throws IOException {
        var fullKey = textKeyPrefix + key;
        return s3ObjectRepository.getS3Object(fullKey);
    }

    @Override
    public void deleteImage(String key) throws IOException {
        var fullKey = imageKeyPrefix + key;
        s3ObjectRepository.deleteS3Object(fullKey);
    }

    @Override
    public void deleteTextFile(String key) throws IOException {
        var fullKey = textKeyPrefix + key;
        s3ObjectRepository.deleteS3Object(fullKey);
    }

    @Override
    public boolean isImageExists(String key) throws IOException {
        var fullKey = imageKeyPrefix + key;
        try {
            s3ObjectRepository.getS3Object(fullKey);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    @Override
    public boolean isTextFileExists(String key) throws IOException {
        var fullKey = textKeyPrefix + key;
        try {
            s3ObjectRepository.getS3Object(fullKey);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    @Override
    public String parseTextFile(MultipartFile file, Integer numberOfParsedSymbols) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }

        if (resultStringBuilder.toString().isEmpty())
            return null;
        if (resultStringBuilder.length() < numberOfParsedSymbols)
            return resultStringBuilder.toString();
        return resultStringBuilder
                .substring(0, numberOfParsedSymbols);
    }
}
