package com.checkmarx.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Scan {

    String ID;
    String ProjectID;
    String Status;
    String CreatedAt;
    String UpdatedAt;
    Map<String, String> Tags;
    String Initiator;
    String Origin;

    @JsonCreator
    public Scan(@JsonProperty("ID") String ID, @JsonProperty("ProjectID") String ProjectID,
                @JsonProperty("Status") String Status, @JsonProperty("CreatedAt") String CreatedAt,
                @JsonProperty("UpdatedAt") String UpdatedAt, @JsonProperty("Tags") Map<String, String> Tags,
                @JsonProperty("Initiator") String Initiator, @JsonProperty("Origin") String Origin) {
        this.ID = ID;
        this.ProjectID = ProjectID;
        this.Status = Status;
        this.CreatedAt = CreatedAt;
        this.UpdatedAt = UpdatedAt;
        this.Tags = Tags;
        this.Initiator = Initiator;
        this.Origin = Origin;
    }

    public static Scan scanFromLine(String line) {
        return parse(line, new TypeReference<Scan>() {
        });
    }

    public static List<Scan> scanListFromLine(String line) {
        return parse(line, new TypeReference<List<Scan>>() {
        });
    }

    private static <T> T parse(String line, TypeReference<T> typeReference) {
        try {
            return new ObjectMapper().readValue(line, typeReference);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
