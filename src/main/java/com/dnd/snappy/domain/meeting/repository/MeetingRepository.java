package com.dnd.snappy.domain.meeting.repository;

import com.dnd.snappy.domain.meeting.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
}
