package com.checkmarx.ast.ScanResult;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Error  {
    int code;
    String description;

    @JsonCreator
    public Error(
            @JsonProperty("code") int code,
            @JsonProperty("description") String description)
    {
        this.code = code;
        this.description = description;
    }
}
