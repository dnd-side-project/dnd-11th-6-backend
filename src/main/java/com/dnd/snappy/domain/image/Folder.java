package com.dnd.snappy.domain.image;

import lombok.Getter;

@Getter
public enum Folder {
    DEFAULT("upload"),
    SNAP("snap");

    private final String name;

    Folder(String name) {
        this.name = name;
    }
}
