package com.dnd.snappy.controller;

import com.dnd.snappy.common.error.CommonErrorCode;
import com.dnd.snappy.common.error.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtTokenExtractor {

    private static final String PREFIX_BEARER = "Bearer ";
    private static final String ACCESS_TOKEN_HEADER = HttpHeaders.AUTHORIZATION;

    public String extractAccessToken(final HttpServletRequest request) {
        final String accessToken = request.getHeader(ACCESS_TOKEN_HEADER);
        if (isValid(accessToken)) {
            return accessToken.substring(PREFIX_BEARER.length());
        }
        final String logMessage = "인증 실패(액세스 토큰 추출 실패) - 토큰 : " + accessToken;
        throw new BusinessException(CommonErrorCode.JWT_EXTRACT_ERROR, logMessage);
    }

    public Optional<String> extractOptionalAccessToken(final HttpServletRequest request) {
        final String accessToken = request.getHeader(ACCESS_TOKEN_HEADER);
        if (isValid(accessToken)) {
            return Optional.of(accessToken.substring(PREFIX_BEARER.length()));
        }
        return Optional.empty();
    }

    private static boolean isValid(String accessToken) {
        return StringUtils.hasText(accessToken) && accessToken.startsWith(PREFIX_BEARER);
    }
}
