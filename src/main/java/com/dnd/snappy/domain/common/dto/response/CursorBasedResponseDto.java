package com.dnd.snappy.domain.common.dto.response;

import java.util.List;

public record CursorBasedResponseDto<T>(
        Long nextCursorId,
        T data,
        Long count,
        boolean hasNextCursor
) {

    public static <T> CursorBasedResponseDto<T> empty(T emptyList) {
        return new CursorBasedResponseDto<>(0L, emptyList, 0L, false);
    }
}
