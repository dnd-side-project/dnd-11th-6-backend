package com.dnd.snappy.domain.common.dto.request;

import java.util.Optional;

public record CursorBasedRequestDto(
        Optional<Long> cursorId,
        Integer limit
) {
}
