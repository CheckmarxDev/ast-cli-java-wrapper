package com.checkmarx.ast;

public class CxExeception extends RuntimeException{
    public  CxExeception(String errorMessage) {
        super(errorMessage);
    }
}
