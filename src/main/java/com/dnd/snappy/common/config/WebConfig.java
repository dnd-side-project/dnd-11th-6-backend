package com.dnd.snappy.common.config;


import com.dnd.snappy.controller.v1.auth.interceptor.MeetingParticipationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final MeetingParticipationInterceptor meetingParticipationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 이 인터셉터가 적용될 경로 패턴 지정
        registry.addInterceptor(meetingParticipationInterceptor)
                .addPathPatterns("/api/**/meetings/{meetingId}/**")
                .excludePathPatterns("/api/**/meetings/{meetingId}/validate-password/**")
                .excludePathPatterns("/api/**/meetings/{meetingId}/share"); // /meeting/{meetingId} 하위 경로에 모두 적용
    }
}
