package com.dnd.snappy.controller.v1.member.request;

import com.dnd.snappy.domain.member.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ParticipationRequest(
        @Size(min = 1, max = 6, message = "닉네임은 최소 1글자 최대 6글자까지 가능합니다.")
        @NotBlank(message = "nickname은 필수입니다.")
        String nickname,

        @NotNull(message = "role은 필수입니다.")
        Role role
) {
}
