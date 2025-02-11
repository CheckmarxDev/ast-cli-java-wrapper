package com.checkmarx.ast.predicate;

import com.fasterxml.jackson.annotation.JsonCreator;
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
import java.lang.reflect.Field;
import java.util.List;

@Value
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Predicate {

    String id;
    String similarityId;
    String projectId;
    String state;
    String severity;
    String comment;
    String createdBy;
    String createdAt;
    String updatedAt;
    String stateId;

    @JsonCreator
    public Predicate(@JsonProperty("ID") String id, @JsonProperty("SimilarityID") String similarityId,
                     @JsonProperty("ProjectID") String projectId, @JsonProperty("State") String state,
                     @JsonProperty("Severity") String severity, @JsonProperty("Comment") String comment,
                     @JsonProperty("CreatedBy") String createdBy, @JsonProperty("CreatedAt") String createdAt,
                     @JsonProperty("UpdatedAt") String updatedAt, @JsonProperty("StateId") String stateId) {
        this.id = id;
        this.similarityId = similarityId;
        this.projectId = projectId;
        this.state = state;
        this.severity = severity;
        this.comment = comment;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.stateId = stateId;
    }

    public static <T> T fromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructType(Predicate.class));
    }

    public static <T> List<T> listFromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructCollectionType(List.class, Predicate.class));
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

    public static boolean validator(List<String> arguments, Object parsedLine) {
        {
            for (Field field : parsedLine.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    if (field.get(parsedLine) == null && !field.getName().equals("stateId")) {
                        return false;
                    }
                } catch (IllegalAccessException e) {
                    return false;
                }
            }
            return true;
        }
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
