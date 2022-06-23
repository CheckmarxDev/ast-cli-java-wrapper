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
public class DependencyPaths {

    String id;
    String name;
    String version;
    Boolean isDevelopment;
    Boolean isResolved;


    public DependencyPaths(@JsonProperty("id") String id,
                          @JsonProperty("name") String name,
                          @JsonProperty("version") String version,
                          @JsonProperty("isDevelopment") Boolean isDevelopment,
                          @JsonProperty("isResolved") Boolean isResolved) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.isDevelopment = isDevelopment;
        this.isResolved = isResolved;
    }
}
