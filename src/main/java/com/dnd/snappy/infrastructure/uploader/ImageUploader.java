package com.dnd.snappy.infrastructure.uploader;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploader {
    String upload(MultipartFile file);
    void delete(String fileUrl);
}