package com.checkmarx.ast.kicsRealtimeResults.ast.kicsRealtimeResult;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@lombok.Data
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

public class KicsLocation {
    private final String fileName;
    private final String similarityID;
    private final int line;
    private final String issueType;
    private final String searchKey;
    private final int searchLine;
    private final String searchValue;
    private final String expectedValue;
    private final String actualValue;

    public KicsLocation(@JsonProperty("file_name") String fileName,
                        @JsonProperty("similarity_id") String similarityID,
                        @JsonProperty("line") int line,
                        @JsonProperty("issue_type") String issueType,
                        @JsonProperty("search_key") String searchKey,
                        @JsonProperty("search_line") int searchLine,
                        @JsonProperty("search_value") String searchValue,
                        @JsonProperty("expected_value") String expectedValue,
                        @JsonProperty("actual_value") String actualValue) {
        this.fileName = fileName;
        this.similarityID = similarityID;
        this.line = line;
        this.issueType = issueType;
        this.searchKey = searchKey;
        this.searchLine = searchLine;
        this.searchValue = searchValue;
        this.expectedValue = expectedValue;
        this.actualValue = actualValue;
    }
}
