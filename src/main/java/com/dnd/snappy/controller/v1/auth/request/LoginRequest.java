package com.dnd.snappy.controller.v1.auth.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {
}
