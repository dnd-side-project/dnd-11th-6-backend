package com.dnd.snappy.controller.v1.snap.request;

import com.dnd.snappy.domain.common.dto.request.CursorBasedRequestDto;
import java.util.Optional;

public record CursorBasedRequest(
        Long cursorId,
        Integer limit
) {
    public CursorBasedRequestDto toCursorBasedRequestDto() {
        return new CursorBasedRequestDto((cursorId == null || cursorId <= 0) ? Optional.empty() : Optional.of(cursorId), limit == null ? 10 : limit);
    }
}
