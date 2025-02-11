package com.checkmarx.ast.predicate;

import com.checkmarx.ast.utils.JsonParser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.Value;

import java.util.List;

@Value
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomState {
    Long id;
    String name;
    String type;

    public CustomState(@JsonProperty("id") Long id,
                       @JsonProperty("name") String name,
                       @JsonProperty("type") String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public static <T> T fromLine(String line) {
        return JsonParser.parse(line, TypeFactory.defaultInstance().constructType(CustomState.class));
    }

    public static <T> List<T> listFromLine(String line) {
        return JsonParser.parse(line, TypeFactory.defaultInstance().constructCollectionType(List.class, CustomState.class));
    }
}
