package com.checkmarx.ast.results.structure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.util.List;

@Data
@Builder
@Value
@EqualsAndHashCode
@ToString
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CxResultOutput {

    int totalCount;
    List<CxResult> results;

    @JsonCreator
    public CxResultOutput(@JsonProperty("totalCount") int totalCount, @JsonProperty("results") List<CxResult> results) {
        this.totalCount = totalCount;
        this.results = results;
    }
}
