package com.dnd.snappy.domain.member.service;

import com.dnd.snappy.domain.token.dto.Tokens;
import com.dnd.snappy.domain.member.dto.response.ParticipationResponseDto;
import com.dnd.snappy.domain.member.entity.Role;
import com.dnd.snappy.domain.token.service.TokenService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final MemberService memberService;

    private final MemberMeetingService memberMeetingService;

    private final TokenService tokenService;

    @Transactional
    public ParticipationResponseDto participate(Long memberId, Long meetingId, String nickname, Role role) {
        // memberId가 없을때 -> 지금 진행하고 있는 모임이 없을경우(access token or refresh token이 없을때?)
        memberId = Optional.ofNullable(memberId)
                .map(memberService::findMemberById)
                .orElseGet(memberService::createMember);
        memberMeetingService.joinMeeting(memberId, meetingId, nickname, role);
        Tokens tokens = tokenService.createTokens(memberId);

        return new ParticipationResponseDto(
                memberId,
                tokens.accessToken(),
                tokens.refreshToken()
        );
    }
}
