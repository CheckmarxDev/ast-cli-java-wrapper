package com.checkmarx.ast.wrapper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;

@Data
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class CxBaseObject {
    String ID;
    String CreatedAt;
    String UpdatedAt;
    Map<String, String> Tags;

    @JsonCreator
    public CxBaseObject(@JsonProperty("ID") String ID,
                        @JsonProperty("CreatedAt") String createdAt,
                        @JsonProperty("UpdatedAt") String updatedAt,
                        @JsonProperty("Tags") Map<String, String> tags) {
        this.ID = ID;
        this.CreatedAt = createdAt;
        this.UpdatedAt = updatedAt;
        this.Tags = tags;
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
