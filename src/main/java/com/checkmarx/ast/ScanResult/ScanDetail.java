package com.checkmarx.ast.ScanResult;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.ToString;
import lombok.Value;

@Value
@ToString(callSuper = true)
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScanDetail {

    int ruleID;
    String language;
    String queryName;
    String severity;
    String fileName;
    int line;
    int length;
    String remediation;
    String description;

    @JsonCreator
    public ScanDetail(
            @JsonProperty("rule_id") int ruleID,
            @JsonProperty("language") String language,
            @JsonProperty("query_name") String queryName,
            @JsonProperty("severity") String severity,
            @JsonProperty("file_name") String fileName,
            @JsonProperty("line") int line,
            @JsonProperty("length") int length,
            @JsonProperty("remediation") String remediation,
            @JsonProperty("description") String description)
    {
        this.ruleID = ruleID;
        this.language = language;
        this.queryName = queryName;
        this.severity = severity;
        this.fileName = fileName;
        this.line = line;
        this.length = length;
        this.remediation = remediation;
        this.description = description;
    }
}
