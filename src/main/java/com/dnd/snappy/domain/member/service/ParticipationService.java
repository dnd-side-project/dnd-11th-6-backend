package com.dnd.snappy.domain.member.service;

import com.dnd.snappy.domain.token.dto.Tokens;
import com.dnd.snappy.domain.member.dto.response.ParticipationResponseDto;
import com.dnd.snappy.domain.member.entity.Role;
import com.dnd.snappy.domain.token.service.TokenService;
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
    public ParticipationResponseDto participate(
            String refreshToken,
            Long meetingId,
            String nickname,
            Role role
    ) {
        // memberId가 없을때: refresh token 이 없거나, 만료됐을때
        Long memberId = getMemberId(refreshToken);
        memberMeetingService.joinMeeting(memberId, meetingId, nickname, role);
        Tokens tokens = tokenService.createTokens(memberId);

        return new ParticipationResponseDto(
                memberId,
                tokens.accessToken(),
                tokens.refreshToken()
        );
    }

    private Long getMemberId(String refreshToken) {
        return tokenService.extractTokenIgnoringExpiration(refreshToken)
                .map(memberService::findMemberById)
                .orElseGet(memberService::createMember);
    }
}
