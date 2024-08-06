package com.dnd.snappy.domain.meeting.repository;

import com.dnd.snappy.domain.meeting.entity.Meeting;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    Optional<Meeting> findByMeetingLink(String meetingLink);

    boolean existsByMeetingLink(String meetingLink);
}
