package com.dnd.snappy.domain.mission.repository;

import com.dnd.snappy.domain.mission.entity.MissionParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionParticipantRepository extends JpaRepository<MissionParticipant, Long> {
    boolean existsByMissionId(Long missionId);
}

