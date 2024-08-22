package com.dnd.snappy.common.logger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
public class RequestResponseLogger extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        filterChain.doFilter(requestWrapper, responseWrapper);
        long duration = System.currentTimeMillis() - startTime;

        log.info("[Request] {} {}, 쿠키 값: {} \n 요청 바디: {}", request.getMethod(), request.getRequestURI(), getHeaderAndValue(requestWrapper), getBodyAsUtf8String(requestWrapper.getContentAsByteArray()));
        log.info("[Response] Status: {}, Duration: {}ms \n 응답 바디: {}", response.getStatus(), duration, getBodyAsUtf8String(responseWrapper.getContentAsByteArray()));

        responseWrapper.copyBodyToResponse();
    }

    private String getBodyAsUtf8String(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private String getHeaderAndValue(ContentCachingRequestWrapper requestWrapper) {
        String value = requestWrapper.getHeader("cookie");
        return "cookie: " + value;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.contains("/docs") || path.contains("/favicon.ico");
    }
}
