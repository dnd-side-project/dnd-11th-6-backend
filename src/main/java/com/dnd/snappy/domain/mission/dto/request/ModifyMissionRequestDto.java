package com.dnd.snappy.domain.mission.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ModifyMissionRequestDto(
        @NotBlank(message = "미션은 필수 입력값입니다.")
        @Size(min = 3, max = 20, message = "미션은 3자 이상 20자 이하이어야 합니다.")
        String content
) { }
