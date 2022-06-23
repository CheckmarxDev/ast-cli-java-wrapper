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
public class ScaPackageData {

    String id;
    Boolean outdated;
    DependencyPaths dependencyPaths;
    List<String> locations;
    public ScaPackageData(@JsonProperty("id") String id,
                       @JsonProperty("outdated") Boolean outdated,
                       @JsonProperty("locations") List<String> locations,
                       @JsonProperty("dependencyPaths") DependencyPaths dependencyPaths) {
        this.id = id;
        this.outdated = outdated;
        this.dependencyPaths = dependencyPaths;
        this.locations = locations;
    }
}
