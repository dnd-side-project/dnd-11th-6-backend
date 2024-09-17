package com.dnd.snappy.domain.image;

import com.dnd.snappy.common.error.exception.BusinessException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class ImageFile {

    private final MultipartFile file;
    private final String uniqueName;
    private final Folder folder;

    public ImageFile(MultipartFile file, Folder folder) {
        validateNullImage(file);
        validateImage(file);
        this.file = file;
        this.folder = folder;
        this.uniqueName = generateUniqueName(file);
    }

    private void validateNullImage(final MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ImageErrorCode.EMPTY_FILE_EXCEPTION);
        }
    }

    private void validateImage(final MultipartFile file) {
        if(!file.getContentType().startsWith("image")) {
            throw new BusinessException(ImageErrorCode.NO_IMAGE);
        }
    }

    private String generateUniqueName(final MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(fileName);

        return String.format("%s/%s.%s", folder.getName(), UUID.randomUUID(), extension);
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public String getContentType() {
        return file.getContentType();
    }

    public long getSize() {
        return file.getSize();
    }

    public InputStream getInputStream() throws IOException {
        return file.getInputStream();
    }
}
