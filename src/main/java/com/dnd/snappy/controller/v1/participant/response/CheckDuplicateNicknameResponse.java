package com.dnd.snappy.controller.v1.participant.response;

public record CheckDuplicateNicknameResponse(
        boolean isAvailableNickname
) {
}
