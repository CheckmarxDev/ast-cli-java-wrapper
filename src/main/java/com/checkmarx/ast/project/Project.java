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

    String Name;
    List<String> Groups;

    @JsonCreator
    public Project(@JsonProperty("ID") String ID,
                   @JsonProperty("Name") String Name,
                   @JsonProperty("CreatedAt") String CreatedAt,
                   @JsonProperty("UpdatedAt") String UpdatedAt,
                   @JsonProperty("Tags") Map<String, String> Tags,
                   @JsonProperty("Groups") List<String> Groups) {
        super(ID, CreatedAt, UpdatedAt, Tags);
        this.Name = Name;
        this.Groups = Groups;
    }

    public static <T> T fromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructType(Project.class));
    }

    public static <T> List<T> listFromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructCollectionType(List.class, Project.class));
    }
}
