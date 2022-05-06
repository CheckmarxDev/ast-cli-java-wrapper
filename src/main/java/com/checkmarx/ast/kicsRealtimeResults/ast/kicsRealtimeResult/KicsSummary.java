package com.checkmarx.ast.kicsRealtimeResults.ast.kicsRealtimeResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


@lombok.Data
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

public class KicsSummary {
    private final int high;
    private final int medium;
    private final int low;
    private final int info;

    public KicsSummary(@JsonProperty("HIGH") int high,
                  @JsonProperty("MEDIUM") int medium,
                  @JsonProperty("LOW") int low,
                  @JsonProperty("INFO") int info) {
        this.high = high;
        this.medium = medium;
        this.low = low;
        this.info = info;
    }
}
