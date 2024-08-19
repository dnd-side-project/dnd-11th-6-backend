package com.dnd.snappy.domain.common.dto.response;

import java.util.List;

public record CursorBasedResponseDto<T>(
        Long lastCursorId,
        List<T> data,
        Long count,
        boolean hasNextCursor
) {

    public static <T> CursorBasedResponseDto<T> empty() {
        return new CursorBasedResponseDto<>(0L, List.of(), 0L, false);
    }

    public static <T> CursorBasedResponseDto<T> of(
            Long lastCursorId,
            List<T> data,
            Long count,
            boolean hasNextCursor
    ) {
        return new CursorBasedResponseDto<>(lastCursorId, data, count, hasNextCursor);
    }
}
