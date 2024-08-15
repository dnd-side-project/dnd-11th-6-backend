package com.dnd.snappy.domain.participant.service;

import com.dnd.snappy.domain.participant.dto.response.CreateParticipantResponseDto;
import com.dnd.snappy.domain.token.dto.Tokens;
import com.dnd.snappy.domain.participant.dto.response.ParticipationResponseDto;
import com.dnd.snappy.domain.participant.entity.Role;
import com.dnd.snappy.domain.token.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipantService participantService;

    private final TokenService tokenService;

    @Transactional
    public ParticipationResponseDto participate(
            Long meetingId,
            String nickname,
            Role role
    ) {
        CreateParticipantResponseDto createParticipantResponseDto = participantService.createParticipant(meetingId, nickname, role);
        Tokens tokens = tokenService.createTokens(createParticipantResponseDto.participantId());

        return new ParticipationResponseDto(
                createParticipantResponseDto.participantId(),
                createParticipantResponseDto.meetingExpiredDate(),
                tokens.accessToken(),
                tokens.refreshToken()
        );
    }
}
