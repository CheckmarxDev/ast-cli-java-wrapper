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
    String ruleName;
    String severity;
    String fileName;
    int line;
    int problematicLine;
    int length;
    String remediationAdvise;
    String description;

    @JsonCreator
    public ScanDetail(
            @JsonProperty("rule_id") int ruleID,
            @JsonProperty("language") String language,
            @JsonProperty("rule_name") String ruleName,
            @JsonProperty("severity") String severity,
            @JsonProperty("file_name") String fileName,
            @JsonProperty("line") int line,
            @JsonProperty("problematic_line") int problematicLine,
            @JsonProperty("length") int length,
            @JsonProperty("remediation_advise") String remediationAdvise,
            @JsonProperty("description") String description)
    {
        this.ruleID = ruleID;
        this.language = language;
        this.ruleName = ruleName;
        this.severity = severity;
        this.fileName = fileName;
        this.line = line;
        this.problematicLine = problematicLine;
        this.length = length;
        this.remediationAdvise = remediationAdvise;
        this.description = description;
    }
}
