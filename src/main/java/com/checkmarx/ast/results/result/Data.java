package com.checkmarx.ast.results.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.util.List;


@Value
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {

    String queryId;
    String queryName;
    String group;
    String resultHash;
    String languageName;
    String description;
    String platform;
    String issueType;
    String expectedValue;
    String value;
    String fileName;
    int line;
    List<Node> nodes;
    List<PackageData> packageData;

    public Data(@JsonProperty("queryId") String queryId,
                @JsonProperty("queryName") String queryName,
                @JsonProperty("group") String group,
                @JsonProperty("resultHash") String resultHash,
                @JsonProperty("languageName") String languageName,
                @JsonProperty("description") String description,
                @JsonProperty("platform") String platform,
                @JsonProperty("issueType") String issueType,
                @JsonProperty("expectedValue") String expectedValue,
                @JsonProperty("value") String value,
                @JsonProperty("fileName") String fileName,
                @JsonProperty("line") int line,
                @JsonProperty("nodes") List<Node> nodes,
                @JsonProperty("packageData") List<PackageData> packageData) {
        this.queryId = queryId;
        this.queryName = queryName;
        this.group = group;
        this.resultHash = resultHash;
        this.languageName = languageName;
        this.description = description;
        this.platform = platform;
        this.issueType = issueType;
        this.expectedValue = expectedValue;
        this.value = value;
        this.fileName = fileName;
        this.line = line;
        this.nodes = nodes;
        this.packageData = packageData;
    }
}
