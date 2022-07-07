package com.checkmarx.ast.results.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Value;

import java.util.List;

@Value
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DependencyPath {

    String Id;
    String name;
    String version;
    List<String> locations;
    boolean isResolved;
    boolean isDevelopment;


    public DependencyPath(@JsonProperty("Id") String id,
                          @JsonProperty("name") String name,
                          @JsonProperty("version") String version,
                          @JsonProperty("locations") List<String> locations,
                          @JsonProperty("isResolved") boolean isResolved,
                          @JsonProperty("isDevelopment") boolean isDevelopment) {

        Id = id;
        this.name = name;
        this.version = version;
        this.locations = locations;
        this.isResolved = isResolved;
        this.isDevelopment = isDevelopment;
    }
}
