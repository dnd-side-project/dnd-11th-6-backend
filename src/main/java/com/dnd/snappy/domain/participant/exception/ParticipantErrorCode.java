package com.dnd.snappy.domain.participant.exception;

import com.dnd.snappy.common.error.ErrorCode;
import com.dnd.snappy.common.error.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ParticipantErrorCode implements ErrorCodeInterface {
    ALREADY_PARTICIPATE_MEETING(HttpStatus.BAD_REQUEST, "ALREADY_PARTICIPATE_MEETING", "이미 참여중인 모임입니다."),
    DUPLICATED_NICKNAME(HttpStatus.CONFLICT, "DUPLICATED_NICKNAME", "모임에서 사용중인 닉네임입니다."),
    NOT_PARTICIPATING_MEETING(HttpStatus.FORBIDDEN, "NOT_PARTICIPATING_MEETING", "모임에 참여중인 참가자가 아닙니다."),
    NOT_FOUND_PARTICIPANT(HttpStatus.NOT_FOUND, "NOT_FOUND_PARTICIPANT", "참가자를 찾을 수 없습니다."),
    EXCEED_MAX_SHOOT_COUNT(HttpStatus.BAD_REQUEST, "EXCEED_MAX_SHOOT_COUNT", "촬영 가능한 횟수를 초과했습니다.")
    ;
    private final HttpStatus status;

    private final String errorResponseCode;

    private final String message;

    @Override
    public ErrorCode toErrorCode() {
        return ErrorCode.builder()
                .status(status)
                .errorResponseCode(errorResponseCode)
                .message(message)
                .build();
    }
}
