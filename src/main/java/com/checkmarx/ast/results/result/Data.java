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
    List<Node> nodes;
    List<PackageData> packageData;

    public Data(@JsonProperty("queryId") String queryId,
                @JsonProperty("queryName") String queryName,
                @JsonProperty("group") String group,
                @JsonProperty("resultHash") String resultHash,
                @JsonProperty("languageName") String languageName,
                @JsonProperty("description") String description,
                @JsonProperty("nodes") List<Node> nodes,
                @JsonProperty("packageData") List<PackageData> packageData) {
        this.queryId = queryId;
        this.queryName = queryName;
        this.group = group;
        this.resultHash = resultHash;
        this.languageName = languageName;
        this.description = description;
        this.nodes = nodes;
        this.packageData = packageData;
    }
}
