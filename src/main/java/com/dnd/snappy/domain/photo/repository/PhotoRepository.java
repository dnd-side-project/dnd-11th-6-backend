package com.dnd.snappy.domain.photo.repository;

import com.dnd.snappy.domain.photo.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
