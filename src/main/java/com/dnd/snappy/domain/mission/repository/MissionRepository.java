package com.dnd.snappy.domain.mission.repository;

import com.dnd.snappy.domain.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long> {
}
