package com.dnd.snappy.domain.snap.repository;

import com.dnd.snappy.domain.snap.entity.Snap;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SnapRepository extends JpaRepository<Snap, Long> {

    @Query("select s from Snap s where s.id < :cursorId and s.meeting.id = :meetingId order by s.id desc")
    List<Snap> findSnapsInMeetingByCursorId(@Param("cursorId") Long cursorId, @Param("meetingId") Long meetingId, Pageable pageable);

    @Query("select s from RandomMissionSnap s where s.id < :cursorId and s.meeting.id = :meetingId order by s.id desc")
    List<Snap> findRandomMissionSnapsInMeetingByCursorId(@Param("cursorId") Long cursorId, @Param("meetingId") Long meetingId, Pageable pageable);

    @Query("select s from MeetingMissionSnap s where s.id < :cursorId and s.meeting.id = :meetingId order by s.id desc")
    List<Snap> findMeetingMissionSnapsInMeetingByCursorId(@Param("cursorId") Long cursorId, @Param("meetingId") Long meetingId, Pageable pageable);
}
