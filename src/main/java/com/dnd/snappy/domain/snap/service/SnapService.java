package com.dnd.snappy.domain.snap.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.dnd.snappy.common.error.exception.S3Exception;
import com.dnd.snappy.domain.snap.exception.S3ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Component
public class SnapService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public String upload(MultipartFile snap) {
        if (snap.isEmpty() || Objects.isNull(snap.getOriginalFilename())) {
            throw new S3Exception(S3ErrorCode.EMPTY_FILE_EXCEPTION);
        }
        validateSnapFileExtension(snap.getOriginalFilename());
        return this.uploadSnapToS3(snap);
    }

    private void validateSnapFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new S3Exception(S3ErrorCode.NO_FILE_EXTENTION);
        }

        String extension = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtensionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtensionList.contains(extension)) {
            throw new S3Exception(S3ErrorCode.INVALID_FILE_EXTENTION);
        }
    }

    private String uploadSnapToS3(MultipartFile snap) {
        String originalFilename = snap.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + "_" + originalFilename;

        try (InputStream is = snap.getInputStream();
             ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(IOUtils.toByteArray(is))) {

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/" + extension);
            metadata.setContentLength(byteArrayInputStream.available());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata);
            amazonS3.putObject(putObjectRequest);

        } catch (IOException e) {
            throw new S3Exception(S3ErrorCode.IO_EXCEPTION_ON_SNAP_UPLOAD);
        }

        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }

    public void deleteSnapFromS3(String snapAddress) {
        String key = getKeyFromSnapAddress(snapAddress);
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        } catch (Exception e) {
            throw new S3Exception(S3ErrorCode.IO_EXCEPTION_ON_SNAP_DELETE);
        }
    }

    private String getKeyFromSnapAddress(String snapAddress) {
        try {
            URL url = new URL(snapAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1); // 맨 앞의 '/' 제거
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new S3Exception(S3ErrorCode.IO_EXCEPTION_ON_SNAP_DELETE);
        }
    }
}
