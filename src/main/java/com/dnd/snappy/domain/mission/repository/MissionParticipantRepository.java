package com.dnd.snappy.domain.mission.repository;

import com.dnd.snappy.domain.mission.entity.Mission;
import com.dnd.snappy.domain.mission.entity.MissionParticipant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MissionParticipantRepository extends JpaRepository<MissionParticipant, Long> {
  
    boolean existsByMissionId(Long missionId);

    boolean existsByMissionIdAndParticipantId(Long missionId, Long participantId);

    @Query("SELECT m FROM Mission m LEFT JOIN MissionParticipant mp ON m.id = mp.mission.id AND mp.participant.id = :participantId WHERE mp.id IS NULL AND m.meeting.id = :meetingId")
    List<Mission> findNotCompletedMissions(@Param("meetingId") Long meetingId, @Param("participantId") Long participantId);

    @Query("SELECT m FROM Mission m INNER JOIN MissionParticipant mp ON m.id = mp.mission.id WHERE mp.participant.id = :participantId")
    List<Mission> findCompletedMissions(Long participantId);
}
