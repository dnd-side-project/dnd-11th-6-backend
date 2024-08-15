package com.dnd.snappy.domain.token.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {
    ACCESS_TOKEN("ACCESS_TOKEN"),
    REFRESH_TOKEN("REFRESH_TOKEN")
    ;

    private final String value;
}
