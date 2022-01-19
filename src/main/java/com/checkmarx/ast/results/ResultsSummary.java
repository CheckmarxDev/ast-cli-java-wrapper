package com.checkmarx.ast.results;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Value;

@Value
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultsSummary {

    private int totalIssues;
    private int highIssues;
    private int mediumIssues;
    private int lowIssues;
    private int sastIssues;
    private int scaIssues;
    private int kicsIssues;
    private String riskStyle;
    private String riskMessage;
    private String status;
    private String scanId;
    private String scanData;
    private String scanTime;
    private String createdAt;
    private String projectId;
    private String baseURI;


    public ResultsSummary(@JsonProperty("TotalIssues") int totalIssues,
                @JsonProperty("HighIssues") int highIssues,
                @JsonProperty("MediumIssues") int mediumIssues,
                @JsonProperty("LowIssues") int lowIssues,
                @JsonProperty("SastIssues") int sastIssues,
                @JsonProperty("ScaIssues") int scaIssues,
                @JsonProperty("KicsIssues") int kicsIssues,
                @JsonProperty("RiskStyle") String riskStyle,
                @JsonProperty("RiskMessage") String riskMessage,
                @JsonProperty("Status") String status,
                @JsonProperty("ScanId") String scanId,
                @JsonProperty("ScanData") String scanData,
                @JsonProperty("scanTime") String scanTime,
                @JsonProperty("createdAt") String createdAt,
                @JsonProperty("projectId") String projectId,
                @JsonProperty("baseURI") String baseURI) {
        this.totalIssues = totalIssues;
        this.highIssues = highIssues;
        this.mediumIssues = mediumIssues;
        this.lowIssues = lowIssues;
        this.sastIssues = sastIssues;
        this.scaIssues = scaIssues;
        this.kicsIssues = kicsIssues;
        this.riskStyle = riskStyle;
        this.riskMessage = riskMessage;
        this.status = status;
        this.scanId = scanId;
        this.scanData = scanData;
        this.scanTime = scanTime;
        this.createdAt = createdAt;
        this.projectId = projectId;
        this.baseURI = baseURI;
    }
}
