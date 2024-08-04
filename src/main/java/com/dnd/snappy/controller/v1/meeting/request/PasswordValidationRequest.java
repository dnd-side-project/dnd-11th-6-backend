package com.dnd.snappy.controller.v1.meeting.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordValidationRequest(
        @NotBlank(message = "password는 필수입니다.")
        String password
) {
}
