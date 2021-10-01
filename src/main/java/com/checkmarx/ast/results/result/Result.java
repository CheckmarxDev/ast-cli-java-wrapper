package com.checkmarx.ast.results.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Value;

@Value
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

    String type;
    String id;
    String similarityId;
    String status;
    String state;
    String severity;
    String created;
    String firstFoundAt;
    String foundAt;
    String firstScan;
    String firstScanId;
    String publishedAt;
    String recommendations;
    Data data;
    Comments comments;
    VulnerabilityDetails vulnerabilityDetails;

    public Result(@JsonProperty("type") String type,
                  @JsonProperty("id") String id,
                  @JsonProperty("similarityId") String similarityId,
                  @JsonProperty("status") String status,
                  @JsonProperty("state") String state,
                  @JsonProperty("severity") String severity,
                  @JsonProperty("created") String created,
                  @JsonProperty("firstFoundAt") String firstFoundAt,
                  @JsonProperty("foundAt") String foundAt,
                  @JsonProperty("firstScan") String firstScan,
                  @JsonProperty("firstScanId") String firstScanId,
                  @JsonProperty("publishedAt") String publishedAt,
                  @JsonProperty("recommendations") String recommendations,
                  @JsonProperty("data") Data data,
                  @JsonProperty("comments") Comments comments,
                  @JsonProperty("vulnerabilityDetails") VulnerabilityDetails vulnerabilityDetails) {
        this.type = type;
        this.id = id;
        this.similarityId = similarityId;
        this.status = status;
        this.state = state;
        this.severity = severity;
        this.created = created;
        this.firstFoundAt = firstFoundAt;
        this.foundAt = foundAt;
        this.firstScan = firstScan;
        this.firstScanId = firstScanId;
        this.publishedAt = publishedAt;
        this.recommendations = recommendations;
        this.data = data;
        this.comments = comments;
        this.vulnerabilityDetails = vulnerabilityDetails;
    }
}
