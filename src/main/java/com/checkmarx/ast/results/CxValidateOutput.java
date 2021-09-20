package com.checkmarx.ast.results;


import lombok.Data;

@Data
public class CxValidateOutput {
    private int exitCode;
    private String message;
}
