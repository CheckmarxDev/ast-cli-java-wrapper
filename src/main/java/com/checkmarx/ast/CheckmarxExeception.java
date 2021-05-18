package com.checkmarx.ast;

public class CheckmarxExeception extends RuntimeException{
    public  CheckmarxExeception(String errorMessage) {
        super(errorMessage);
    }
}
