package com.dnd.snappy.domain.member.service;

import com.dnd.snappy.domain.member.repository.MemberMeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberMeetingService {

    private final MemberMeetingRepository memberMeetingRepository;

}
