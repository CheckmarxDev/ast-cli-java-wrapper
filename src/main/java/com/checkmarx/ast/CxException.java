package com.checkmarx.ast;

public class CxException extends RuntimeException{
    public CxException(String errorMessage) {
        super(errorMessage);
    }
}
