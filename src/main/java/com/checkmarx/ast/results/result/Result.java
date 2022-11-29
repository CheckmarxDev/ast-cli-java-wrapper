package com.checkmarx.ast.results.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


@lombok.Data
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

    private final String type;
    private final String scaType;
    private final String id;
    private final String similarityId;
    private final String status;
    private String state;
    private String severity;
    private final String created;
    private final String firstFoundAt;
    private final String foundAt;
    private final String firstScan;
    private final String firstScanId;
    private final String publishedAt;
    private final String recommendations;
    private final String description;
    private final String descriptionHTML;
    private final Data data;
    private final Comments comments;
    private final VulnerabilityDetails vulnerabilityDetails;

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
                  @JsonProperty("description") String description,
                  @JsonProperty("descriptionHTML") String descriptionHTML,
                  @JsonProperty("data") Data data,
                  @JsonProperty("comments") Comments comments,
                  @JsonProperty("vulnerabilityDetails") VulnerabilityDetails vulnerabilityDetails,
                  @JsonProperty("scaType") String scaType) {
        this.type = type;
        this.scaType=scaType;
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
        this.description = description;
        this.descriptionHTML = descriptionHTML;
        this.data = data;
        this.comments = comments;
        this.vulnerabilityDetails = vulnerabilityDetails;
    }
}
