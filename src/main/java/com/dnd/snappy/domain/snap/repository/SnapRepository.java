package com.dnd.snappy.domain.snap.repository;

import com.dnd.snappy.domain.snap.dto.response.SnapResponseDto;
import com.dnd.snappy.domain.snap.entity.Snap;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SnapRepository extends JpaRepository<Snap, Long> {

    Optional<Snap> findFirstByMeetingIdOrderByIdDesc(@Param("meetingId") Long meetingId);

    Long countByMeetingId(@Param("meetingId") Long meetingId);

    @Query("""
        select new com.dnd.snappy.domain.snap.dto.response.SnapResponseDto(s.id, s.snapUrl, s.dtype)
        from Snap s
        where s.id < :cursorId
        and s.meeting.id = :meetingId
        order by s.id desc
    """)
    List<SnapResponseDto> findSnapsInMeetingByCursorId(@Param("cursorId") Long cursorId, @Param("meetingId") Long meetingId, Pageable pageable);

    Optional<Snap> findFirstByMeetingIdAndParticipantIdOrderByIdDesc(@Param("meetingId") Long meetingId, @Param("participantId") Long participantId);

    Long countByMeetingIdAndParticipantId(@Param("meetingId") Long meetingId, @Param("participantId") Long participantId);

    @Query("""
        select new com.dnd.snappy.domain.snap.dto.response.SnapResponseDto(s.id, s.snapUrl, s.dtype)
        from Snap s
        where s.id < :cursorId
        and s.meeting.id = :meetingId
        and s.participant.id = :participantId
        order by s.id desc
    """)
    List<SnapResponseDto> findParticipantSnapsInMeetingByCursorId(
            @Param("cursorId") Long cursorId,
            @Param("meetingId") Long meetingId,
            @Param("participantId") Long participantId,
            Pageable pageable
    );

    @Query("""
        select s
        from Snap s
        join fetch s.participant
        where s.id = :snapId
    """)
    Optional<Snap> findSnapByIdWithParticipant(@Param("snapId") Long snapId);

}
