package com.dnd.snappy.controller.v1.auth;

import com.dnd.snappy.common.error.CommonErrorCode;
import com.dnd.snappy.common.error.exception.BusinessException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerMapping;

@Component
public class JwtTokenExtractor {

    private static final String PATH_VARIABLE_KEY = "meetingId";

    private final AuthTokenCookieNameGenerator authTokenCookieNameGenerator;

    public JwtTokenExtractor(AuthTokenCookieNameGenerator authTokenCookieNameGenerator) {
        this.authTokenCookieNameGenerator = authTokenCookieNameGenerator;
    }

    public String extractAccessToken(final HttpServletRequest request) {
        final Long meetingId = getMeetingId(request);
        String accessTokenCookieName = authTokenCookieNameGenerator.generateAccessToken(meetingId);

        Cookie accessTokenCookie = getCookie(request, accessTokenCookieName);

        return accessTokenCookie.getValue();
    }

    private Long getMeetingId(HttpServletRequest request) {
        final Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        return Long.parseLong(pathVariables.get(PATH_VARIABLE_KEY));
    }

    private Cookie getCookie(HttpServletRequest request, String cookieName) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findFirst()
                .orElseThrow(() -> new BusinessException(CommonErrorCode.JWT_EXTRACT_ERROR));
    }
}
