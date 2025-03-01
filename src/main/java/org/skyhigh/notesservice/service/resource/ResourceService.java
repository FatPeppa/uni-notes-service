package org.skyhigh.notesservice.service.resource;

import org.skyhigh.notesservice.data.entity.MediaTypeEnum;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResourceService {
    String uploadImage(String key, MultipartFile image) throws IOException;
    String uploadTextFile(String key, MultipartFile text) throws IOException;
    boolean isFileSupported(MultipartFile file) throws IOException;
    MediaTypeEnum getFileType(MultipartFile file) throws IOException;
    boolean isFileTypeSupported(MediaTypeEnum file) throws IOException;
    boolean checkFileSizeLimited(MultipartFile file, SizeCheckableFileType sizeCheckableFileType) throws IOException;
    byte[] downloadImage(String key) throws IOException;
    byte[] downloadTextFile(String key) throws IOException;
    void deleteImage(String key) throws IOException;
    void deleteTextFile(String key) throws IOException;
    boolean isImageExists(String key) throws IOException;
    boolean isTextFileExists(String key) throws IOException;
    String parseTextFile(MultipartFile file, Integer numberOfParsedSymbols) throws IOException;
}