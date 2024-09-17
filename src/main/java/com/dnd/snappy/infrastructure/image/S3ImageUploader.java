package com.dnd.snappy.infrastructure.image;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dnd.snappy.common.error.exception.ImageException;
import com.dnd.snappy.domain.image.ImageErrorCode;
import com.dnd.snappy.domain.image.ImageFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class S3ImageUploader implements ImageUploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    @Override
    public String upload(ImageFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, file.getUniqueName(), inputStream, metadata);
            amazonS3.putObject(putObjectRequest);

        } catch (IOException e) {
            throw new ImageException(ImageErrorCode.IO_EXCEPTION_ON_UPLOAD);
        }

        return file.getUniqueName();
    }

}
