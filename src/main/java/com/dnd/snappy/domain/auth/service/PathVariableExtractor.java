package com.dnd.snappy.domain.auth.service;

import static com.dnd.snappy.common.error.CommonErrorCode.BAD_REQUEST;

import com.dnd.snappy.common.error.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

@Component
public class PathVariableExtractor {

    private static final String MEETING_ID_KEY = "meetingId";

    public Long extractMeetingId(HttpServletRequest request) {
        final Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVariables == null || Objects.isNull(pathVariables.get(MEETING_ID_KEY))) {
            throw new BusinessException(BAD_REQUEST, "No path variables meetingId found in request");
        }

        String meetingId = pathVariables.get(MEETING_ID_KEY);
        try {
            return Long.parseLong(pathVariables.get(MEETING_ID_KEY));
        } catch (NumberFormatException e) {
            throw new BusinessException(BAD_REQUEST, "Invalid format for meetingId: " + meetingId);
        }
    }
}
