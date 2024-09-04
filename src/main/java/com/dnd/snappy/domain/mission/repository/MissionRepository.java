package com.dnd.snappy.domain.mission.repository;

import com.dnd.snappy.domain.mission.dto.response.LeaderMeetingMissionDetailResponseDto;
import com.dnd.snappy.domain.mission.entity.Mission;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MissionRepository extends JpaRepository<Mission, Long> {

    Optional<Mission> findByIdAndMeetingId(Long id, Long meetingId);

    List<Mission> findAllByMeetingId(Long meetingId);

    @Query("""
    SELECT new com.dnd.snappy.domain.mission.dto.response.LeaderMeetingMissionDetailResponseDto(m.id, MAX(m.content),
        CASE WHEN MAX(mp.id) IS NOT NULL THEN TRUE ELSE FALSE END)
    FROM Mission m
    JOIN MissionParticipant mp ON mp.mission.id = m.id
    WHERE m.meeting.id = :meetingId
    group by m.id
    """)
    List<LeaderMeetingMissionDetailResponseDto> findLeaderMeetingMissions(@Param("meetingId") Long meetingId);

}