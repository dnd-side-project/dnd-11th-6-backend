package com.dnd.snappy.domain.mission.repository;

import com.dnd.snappy.domain.mission.entity.RandomMission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RandomMissionRepository extends JpaRepository<RandomMission, Integer> {
}
