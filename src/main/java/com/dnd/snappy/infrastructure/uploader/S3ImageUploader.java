package com.dnd.snappy.infrastructure.uploader;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
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
            throw new RuntimeException("파일이 비어있거나 이름이 없습니다.");
        }
        validateFileExtension(file.getOriginalFilename());
        return uploadFileToS3(file);
    }

    private void validateFileExtension(String filename) {
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
        String extension = getFileExtension(filename);

        if (!allowedExtensions.contains(extension)) {
            throw new RuntimeException("유효하지 않은 파일 확장자입니다.");
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
            throw new RuntimeException("S3 업로드 중 오류가 발생했습니다.");
        }

        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }

    @Override
    public void delete(String fileUrl) {
        String key = getKeyFromFileUrl(fileUrl);
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
    }

    private String getKeyFromFileUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            return url.getPath().substring(1); // 맨 앞의 '/' 제거
        } catch (Exception e) {
            throw new RuntimeException("파일 URL에서 키를 가져오는 중 오류가 발생했습니다.");
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex + 1).toLowerCase();
    }
}