package com.checkmarx.ast.predicate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

@Value
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomState {
    private Long id;
    private String name;
    private String type;

    public CustomState(@JsonProperty("id") Long id,
                       @JsonProperty("name") String name,
                       @JsonProperty("type") String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public static <T> T fromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructType(CustomState.class));
    }

    public static <T> List<T> listFromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructCollectionType(List.class, CustomState.class));
    }

    protected static <T> T parse(String line, JavaType type) {
        T result = null;
        try {
            if (!StringUtils.isBlank(line) && isValidJSON(line)) {
                result = new ObjectMapper().readValue(line, type);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static boolean isValidJSON(final String json) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
