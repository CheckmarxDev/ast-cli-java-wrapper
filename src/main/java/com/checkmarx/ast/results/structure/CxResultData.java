package com.checkmarx.ast.results.structure;

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
public class CxResultData {

    String queryId;
    String queryName;
    String group;
    String resultHash;
    String languageName;
    String description;
    List<CxResultDataNode> nodes;
    List<CxResultPackageData> packageData;

    public CxResultData(@JsonProperty("queryId") String queryId,
                        @JsonProperty("queryName") String queryName,
                        @JsonProperty("group") String group,
                        @JsonProperty("resultHash") String resultHash,
                        @JsonProperty("languageName") String languageName,
                        @JsonProperty("description") String description,
                        @JsonProperty("nodes") List<CxResultDataNode> nodes,
                        @JsonProperty("packageData") List<CxResultPackageData> packageData) {
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
