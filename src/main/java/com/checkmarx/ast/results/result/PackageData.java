package com.checkmarx.ast.results.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Value
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PackageData {

    String comment;
    String type;
    String url;

    public PackageData(@JsonProperty("comment") String comment,
                       @JsonProperty("type") String type,
                       @JsonProperty("url") String url) {
        this.comment = comment;
        this.type = type;
        this.url = url;
    }
}
