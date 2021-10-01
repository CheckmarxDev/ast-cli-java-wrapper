package com.checkmarx.ast.wrapper;

import lombok.Value;

@Value
public class CLIOutput<T> {
    int exitCode;
    T output;
}
