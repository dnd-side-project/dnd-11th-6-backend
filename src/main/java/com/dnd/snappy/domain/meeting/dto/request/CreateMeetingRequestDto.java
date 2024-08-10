package com.dnd.snappy.domain.meeting.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

public record CreateMeetingRequestDto(
        @NotBlank(message = "모임명은 필수 입력값입니다.")
        String name,

        @NotBlank(message = "모임 설명은 필수 입력값입니다.")
        String description,

        @NotNull(message = "시작일은 필수 입력값입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime startDate,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime endDate,

        @NotBlank(message = "컬러칩 코드는 필수 입력값입니다.")
        String symbolColor,

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Size(max = 4, message = "비밀번호는 4글자 이내여야 합니다.")
        String password,

        @NotBlank(message = "모임장 비밀번호는 필수 입력값입니다.")
        @Size(max = 4, message = "모임장 비밀번호는 4글자 이내여야 합니다.")
        String adminPassword
) { }
