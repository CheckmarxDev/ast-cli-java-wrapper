package com.checkmarx.ast.results;

import com.checkmarx.ast.results.result.Result;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Value;

import java.util.List;

@Value
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Results {

    int totalCount;
    List<Result> results;

    @JsonCreator
    public Results(@JsonProperty("totalCount") int totalCount, @JsonProperty("results") List<Result> results) {
        this.totalCount = totalCount;
        this.results = results;
    }
}
