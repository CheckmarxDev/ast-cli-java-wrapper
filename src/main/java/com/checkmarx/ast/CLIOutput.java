package com.checkmarx.ast;

import lombok.Value;

@Value
public class CLIOutput<T> {
    int exitCode;
    T output;
}
