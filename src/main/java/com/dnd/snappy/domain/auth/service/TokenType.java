package com.dnd.snappy.domain.auth.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {
    ACCESS_TOKEN,
    REFRESH_TOKEN;
}
