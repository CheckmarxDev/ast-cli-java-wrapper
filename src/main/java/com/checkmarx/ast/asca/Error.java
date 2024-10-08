package com.checkmarx.ast.asca;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class Error  {
    public int code;
    public String description;

    @JsonCreator
    public Error(
            @JsonProperty("code") int code,
            @JsonProperty("description") String description)
    {
        this.code = code;
        this.description = description;
    }
}
