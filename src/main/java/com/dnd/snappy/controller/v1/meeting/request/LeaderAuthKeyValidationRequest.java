package com.dnd.snappy.controller.v1.meeting.request;

import jakarta.validation.constraints.NotBlank;

public record LeaderAuthKeyValidationRequest(
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password,
        @NotBlank(message = "관리자 인증키는 필수입니다.")
        String leaderAuthKey
) {
}
