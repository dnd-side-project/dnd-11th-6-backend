package com.dnd.snappy.domain.snap.repository;

import com.dnd.snappy.domain.snap.entity.Snap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnapRepository extends JpaRepository<Snap, Long> {
}
