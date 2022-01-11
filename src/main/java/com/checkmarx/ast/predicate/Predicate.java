package com.checkmarx.ast.predicate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
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
public class Predicate {

    String ID;
    String SimilarityID;
    String ProjectID;
    String State;
    String Severity;
    String Comment;
    String CreatedBy;
    String CreatedAt;
    String UpdatedAt;

    @JsonCreator
    public Predicate(@JsonProperty("ID") String id, @JsonProperty("SimilarityID") String similarityID,
                     @JsonProperty("ProjectID") String projectID, @JsonProperty("State") String state,
                     @JsonProperty("Severity") String severity, @JsonProperty("Comment") String comment,
                     @JsonProperty("CreatedBy") String createdBy, @JsonProperty("CreatedAt") String CreatedAt,
                     @JsonProperty("UpdatedAt") String UpdatedAt) {
        this.ID = id;
        this.SimilarityID = similarityID;
        this.ProjectID = projectID;
        this.State = state;
        this.Severity = severity;
        this.Comment = comment;
        this.CreatedBy = createdBy;
        this.CreatedAt = CreatedAt;
        this.UpdatedAt = UpdatedAt;
    }

    public static <T> T fromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructType(Predicate.class));
    }

    public static <T> List<T> listFromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructCollectionType(List.class, Predicate.class));
    }

    protected static <T> T parse(String line, JavaType type) {
        T result = null;
        if (!StringUtils.isBlank(line) && isValidJSON(line)) {
            try {
                result = new ObjectMapper().readValue(line, type);
            } catch (JsonProcessingException ignored) {

            }
        }
        return result;
    }

    private static boolean isValidJSON(final String json) {
        boolean valid = false;
        try {
            final JsonParser parser = new ObjectMapper().createParser(json);
            //noinspection StatementWithEmptyBody
            while (parser.nextToken() != null) {
            }
            valid = true;
        } catch (IOException ignored) {
        }
        return valid;
    }

}
