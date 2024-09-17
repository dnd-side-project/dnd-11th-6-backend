package com.dnd.snappy.infrastructure.image;

import com.dnd.snappy.domain.image.ImageFile;

public interface ImageUploader {
    String upload(ImageFile file);
}