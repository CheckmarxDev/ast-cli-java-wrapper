package com.checkmarx.ast.wrapper;

import lombok.Value;

@Value
public class CxOutput<T> {
    int exitCode;
    T output;
}
