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
public class ScaPackageCollection {

    String Id;
    List<String> locations;
    List<DependencyPath> dependencyPathArray;
    boolean outdated;


    public ScaPackageCollection(@JsonProperty("Id") String id,
                                @JsonProperty("locations") List<String> locations,
                                @JsonProperty("dependencyPathArray") List<DependencyPath> dependencyPathArray,
                                @JsonProperty("outdated") boolean outdated) {

        Id = id;
        this.locations = locations;
        this.dependencyPathArray = dependencyPathArray;
        this.outdated = outdated;
    }
}
