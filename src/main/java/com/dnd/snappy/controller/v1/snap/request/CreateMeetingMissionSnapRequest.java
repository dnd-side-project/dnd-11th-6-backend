package com.dnd.snappy.controller.v1.snap.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateMeetingMissionSnapRequest(
        @NotNull(message = "randomMissionId는 필수 입력값입니다.")
        Long missionId,

        @NotNull(message = "shootDate는 필수 입력값입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime shootDate
) {
}
