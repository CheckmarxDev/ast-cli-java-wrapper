package com.checkmarx.ast.wrapper;

import lombok.Getter;

public class CxException extends Exception {
    @Getter
    private final int exitCode;

    public CxException(int exitCode, String message) {
        super(message);
        this.exitCode = exitCode;
    }
}
