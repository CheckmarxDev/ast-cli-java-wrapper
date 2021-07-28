package com.checkmarx.ast.exceptionhandler;

public class CxException extends RuntimeException{
    public CxException(String errorMessage) {
        super(errorMessage);
    }
}
