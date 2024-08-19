package com.dnd.snappy.domain.snap.dto.response;

public record SnapResponseDto(
        Long snapId,
        String snapUrl,
        String type
) {
}
