package com.dnd.snappy.controller.v1.auth.resolver.config;

import com.dnd.snappy.controller.v1.auth.resolver.AuthInfoArgumentResolver;
import com.dnd.snappy.controller.v1.auth.resolver.TokenRefreshArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class ResolverConfig implements WebMvcConfigurer {

    private final AuthInfoArgumentResolver authInfoArgumentResolver;
    private final TokenRefreshArgumentResolver tokenRefreshArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authInfoArgumentResolver);
        resolvers.add(tokenRefreshArgumentResolver);
    }
}
