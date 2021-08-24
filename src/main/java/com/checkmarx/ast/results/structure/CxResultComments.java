package com.checkmarx.ast.results.structure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Data
@Builder
@Value
@EqualsAndHashCode
@ToString
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CxResultComments {
    String comments;

    public CxResultComments(@JsonProperty("comments") String comments) {
        this.comments = comments;
    }
}
