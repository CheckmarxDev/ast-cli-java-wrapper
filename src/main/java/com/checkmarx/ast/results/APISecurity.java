package com.checkmarx.ast.results;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Value;
import java.util.List;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize()
@JsonIgnoreProperties(ignoreUnknown = true)
public class APISecurity {

    @JsonProperty("api_count")
    int apiCount;
    @JsonProperty("total_risks_count")
    int totalRisksCount;
    @JsonProperty("risks")
    List<Integer> risks;

    public APISecurity(@JsonProperty("api_count") int apiCount, @JsonProperty("total_risks_count") int totalRisksCount, @JsonProperty("risks") List<Integer> risks) {
        this.apiCount = apiCount;
        this.totalRisksCount = totalRisksCount;
        this.risks = risks;
    }
}
