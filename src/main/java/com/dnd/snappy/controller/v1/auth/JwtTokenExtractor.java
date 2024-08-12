package com.dnd.snappy.controller.v1.auth;

import static com.dnd.snappy.domain.auth.exception.AuthErrorCode.*;

import com.dnd.snappy.common.error.CommonErrorCode;
import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.domain.auth.exception.AuthErrorCode;
import com.dnd.snappy.domain.token.service.TokenType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

@Component
@RequiredArgsConstructor
public class JwtTokenExtractor {

    private static final String PATH_VARIABLE_KEY = "meetingId";

    private final AuthCookieManager authCookieManager;

    public String extractToken(TokenType tokenType, final HttpServletRequest request) {
        final Long meetingId = getMeetingId(request);
        Cookie cookie = authCookieManager.getCookie(request, tokenType, meetingId);
        if(cookie == null) {
            throw new BusinessException(JWT_EXTRACT_ERROR);
        }

        return cookie.getValue();
    }

    private Long getMeetingId(HttpServletRequest request) {
        final Map<String, String> pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if(pathVariables == null || Objects.isNull(pathVariables.get(PATH_VARIABLE_KEY))) {
            throw new BusinessException(JWT_EXTRACT_ERROR, "No path variables meetingId found in request");
        }
        return Long.parseLong(pathVariables.get(PATH_VARIABLE_KEY));
    }
}
