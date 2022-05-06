package com.checkmarx.ast.kicsRealtimeResults.ast.kicsRealtimeResult;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@lombok.Data
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

public class KicsResult {
    private final String queryName;
    private final String queryID;
    private final String severity;
    private final String platform;
    private final String category;
    private final String description;
    private final List <KicsLocation> locations;

    public KicsResult(@JsonProperty("query_name") String queryName,
                       @JsonProperty("query_id") String queryID,
                       @JsonProperty("severity") String severity,
                       @JsonProperty("platform") String platform,
                       @JsonProperty("category") String category,
                       @JsonProperty("description") String description,
                       @JsonProperty("files") List<KicsLocation> locations) {
        this.queryName = queryName;
        this.queryID = queryID;
        this.severity = severity;
        this.platform = platform;
        this.category = category;
        this.description = description;
        this.locations = locations;
    }
}
