package com.dnd.snappy.domain.meeting.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateMeetingRequestDto(
        @NotBlank(message = "모임명은 필수 입력값입니다.")
        String name,

        @NotBlank(message = "모임 설명은 필수 입력값입니다.")
        String description,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime startDate,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime endDate,

        String symbolColor,

        @Size(max = 4, message = "비밀번호는 4글자 이내여야 합니다.")
        String password,

        String adminPassword
) { }
