package com.checkmarx.ast.results.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Value
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Node {

    String id;
    int line;
    String name;
    int column;
    int length;
    String method;
    int nodeId;
    String domType;
    String fileName;
    String fullName;
    String typeName;
    String methodLine;
    String definitions;

    public Node(@JsonProperty("id") String id,
                @JsonProperty("line") int line,
                @JsonProperty("name") String name,
                @JsonProperty("column") int column,
                @JsonProperty("length") int length,
                @JsonProperty("method") String method,
                @JsonProperty("nodeID") int nodeId,
                @JsonProperty("domType") String domType,
                @JsonProperty("fileName") String fileName,
                @JsonProperty("fullName") String fullName,
                @JsonProperty("typeName") String typeName,
                @JsonProperty("methodLine") String methodLine,
                @JsonProperty("definitions") String definitions) {
        this.id = id;
        this.line = line;
        this.name = name;
        this.column = column;
        this.length = length;
        this.method = method;
        this.nodeId = nodeId;
        this.domType = domType;
        this.fileName = fileName;
        this.fullName = fullName;
        this.typeName = typeName;
        this.methodLine = methodLine;
        this.definitions = definitions;
    }
}
