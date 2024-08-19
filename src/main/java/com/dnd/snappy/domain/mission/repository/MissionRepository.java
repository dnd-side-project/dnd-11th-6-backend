package com.dnd.snappy.domain.mission.repository;

import com.dnd.snappy.domain.mission.entity.Mission;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    Optional<Mission> findByIdAndMeetingId(Long id, Long meetingId);
}
