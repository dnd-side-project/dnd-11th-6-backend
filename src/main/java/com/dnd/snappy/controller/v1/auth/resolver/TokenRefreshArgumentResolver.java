package com.dnd.snappy.controller.v1.auth.resolver;

import com.dnd.snappy.domain.auth.dto.response.TokenInfo;
import com.dnd.snappy.domain.auth.service.JwtTokenExtractor;
import com.dnd.snappy.domain.auth.service.JwtTokenStrategy;
import com.dnd.snappy.domain.auth.service.PathVariableExtractor;
import com.dnd.snappy.domain.token.service.TokenProvider;
import com.dnd.snappy.domain.token.service.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class TokenRefreshArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenStrategy jwtTokenStrategy;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RefreshAuthPrincipal.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        TokenInfo tokenInfo = jwtTokenStrategy.process(request, TokenType.REFRESH_TOKEN);
        return new RefreshAuthInfo(tokenInfo.payload(), tokenInfo.token());
    }
}
