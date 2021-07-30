package com.checkmarx.ast.exceptions;

public class CxException extends RuntimeException{
    public CxException(String errorMessage) {
        super(errorMessage);
    }
}
