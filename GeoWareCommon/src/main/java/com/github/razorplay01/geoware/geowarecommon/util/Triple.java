package com.github.razorplay01.geoware.geowarecommon.util;

import lombok.Getter;

@Getter
public class Triple<L, M, R> {
    private final L left;
    private final M middle;
    private final R right;

    public Triple(L left, M middle, R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }
}
