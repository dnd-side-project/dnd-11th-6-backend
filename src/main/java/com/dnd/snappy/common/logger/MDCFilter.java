package com.dnd.snappy.common.logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MDCFilter implements Filter {
    private final String REQUEST_ID = "request_id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        MDC.put(REQUEST_ID, generateRequestId());
        chain.doFilter(request, response);
        MDC.clear();
    }

    private String generateRequestId() {
        return UUID.randomUUID()
                .toString()
                .substring(0, 8);
    }
}