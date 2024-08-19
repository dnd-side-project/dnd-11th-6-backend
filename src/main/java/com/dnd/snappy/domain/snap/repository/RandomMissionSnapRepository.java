package com.dnd.snappy.domain.snap.repository;

import com.dnd.snappy.domain.snap.dto.response.SnapResponseDto;
import com.dnd.snappy.domain.snap.entity.RandomMissionSnap;
import com.dnd.snappy.domain.snap.entity.Snap;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RandomMissionSnapRepository extends JpaRepository<RandomMissionSnap, Long> {

    Optional<Snap> findFirstByMeetingIdOrderByIdDesc(@Param("meetingId") Long meetingId);

    Long countByMeetingId(@Param("meetingId") Long meetingId);

    @Query("select new com.dnd.snappy.domain.snap.dto.response.SnapResponseDto(s.id, s.snapUrl, s.dtype) from RandomMissionSnap s where s.id < :cursorId and s.meeting.id = :meetingId order by s.id desc")
    List<SnapResponseDto> findRandomMissionSnapsInMeetingByCursorId(@Param("cursorId") Long cursorId, @Param("meetingId") Long meetingId, Pageable pageable);

}
