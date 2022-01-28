package com.checkmarx.ast.project;

import com.checkmarx.ast.wrapper.CxBaseObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project extends CxBaseObject {

    String name;
    List<String> groups;

    @JsonCreator
    public Project(@JsonProperty("ID") String id,
                   @JsonProperty("Name") String name,
                   @JsonProperty("CreatedAt") String createdAt,
                   @JsonProperty("UpdatedAt") String updatedAt,
                   @JsonProperty("Tags") Map<String, String> tags,
                   @JsonProperty("Groups") List<String> groups) {
        super(id, createdAt, updatedAt, tags);
        this.name = name;
        this.groups = groups;
    }

    public static <T> T fromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructType(Project.class));
    }

    public static <T> List<T> listFromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructCollectionType(List.class, Project.class));
    }
}
