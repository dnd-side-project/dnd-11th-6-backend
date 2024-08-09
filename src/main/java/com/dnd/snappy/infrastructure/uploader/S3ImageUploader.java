package com.dnd.snappy.infrastructure.uploader;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.dnd.snappy.common.error.exception.ImageException;
import com.dnd.snappy.infrastructure.exception.ImageErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ImageUploader implements ImageUploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    @Override
    public String upload(MultipartFile file) {
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new ImageException(ImageErrorCode.EMPTY_FILE_EXCEPTION);
        }
        validateFileExtension(file.getOriginalFilename());
        return uploadFileToS3(file);
    }

    private void validateFileExtension(String filename) {
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
        String extension = getFileExtension(filename);

        if (!allowedExtensions.contains(extension)) {
            throw new ImageException(ImageErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    private String uploadFileToS3(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String s3FileName = UUID.randomUUID().toString() + "_" + originalFilename;

        try (InputStream is = file.getInputStream();
             ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(IOUtils.toByteArray(is))) {

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/" + getFileExtension(originalFilename));
            metadata.setContentLength(byteArrayInputStream.available());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata);
            amazonS3.putObject(putObjectRequest);

        } catch (IOException e) {
            throw new ImageException(ImageErrorCode.IO_EXCEPTION_ON_UPLOAD);
        }

        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }

    @Override
    public void delete(String fileUrl) {
        String key = getKeyFromFileUrl(fileUrl);
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IO_EXCEPTION_ON_DELETE);
        }
    }

    private String getKeyFromFileUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            return url.getPath().substring(1); // 맨 앞의 '/' 제거
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IO_EXCEPTION_ON_DELETE);
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex + 1).toLowerCase();
    }
}