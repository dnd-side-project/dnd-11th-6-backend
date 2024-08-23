package com.dnd.snappy.domain.meeting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ModifyMeetingRequestDto(

        @Size(min = 3, max = 15, message = "모임명은 3자 이상 15자 이하이어야 합니다.")
        String name,

        @Size(min = 8, max = 150, message = "모임 설명은 8자 이상 150자 이내여야 합니다.")
        String description,

        String symbolColor
) {
}
