package com.dnd.snappy.domain.participant.repository;

import com.dnd.snappy.domain.participant.dto.response.ParticipantResponseDto;
import com.dnd.snappy.domain.participant.entity.Participant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    boolean existsByNicknameAndMeetingId(String nickname, Long meetingId);

    boolean existsByIdAndMeetingId(Long id, Long meetingId);

    @Query("""
        select new com.dnd.snappy.domain.participant.dto.response.ParticipantResponseDto(p.id, p.role, p.nickname, p.shootCount)
        from Participant p
        where p.meeting.id = :meetingId and p.id > :cursorId
        order by p.id asc
    """)
    List<ParticipantResponseDto> findByMeetingId(@Param("meetingId") Long meetingId, @Param("cursorId") Long cursorId, Pageable pageable);

    Long countByMeetingId(Long meetingId);

    @Query("select p from Participant p join fetch Meeting m")
    Optional<Participant> findByIdWithMeeting();
}
