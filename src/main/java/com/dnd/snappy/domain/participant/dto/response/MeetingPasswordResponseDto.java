package com.dnd.snappy.domain.participant.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

public record MeetingPasswordResponseDto(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String password,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String leaderAuthKey
) {
}
