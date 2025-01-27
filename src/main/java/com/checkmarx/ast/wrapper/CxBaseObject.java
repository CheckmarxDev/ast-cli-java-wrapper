package com.checkmarx.ast.wrapper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    String id;
    String createdAt;
    String updatedAt;
    Map<String, String> tags;

    @JsonCreator
    protected CxBaseObject(@JsonProperty("ID") String id,
                           @JsonProperty("CreatedAt") String createdAt,
                           @JsonProperty("UpdatedAt") String updatedAt,
                           @JsonProperty("Tags") Map<String, String> tags) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.tags = tags;
    }
}
