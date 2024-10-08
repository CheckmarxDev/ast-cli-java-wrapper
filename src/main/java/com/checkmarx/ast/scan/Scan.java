package com.checkmarx.ast.scan;

import com.checkmarx.ast.utils.JsonParser;
import com.checkmarx.ast.wrapper.CxBaseObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Scan extends CxBaseObject {

    String projectId;
    String status;
    String initiator;
    String origin;
    String branch;

    @JsonCreator
    public Scan(@JsonProperty("ID") String id, @JsonProperty("ProjectID") String projectId,
                @JsonProperty("Status") String status, @JsonProperty("CreatedAt") String createdAt,
                @JsonProperty("UpdatedAt") String updatedAt, @JsonProperty("Tags") Map<String, String> tags,
                @JsonProperty("Initiator") String initiator, @JsonProperty("Origin") String origin,
                @JsonProperty("Branch") String branch) {
        super(id, createdAt, updatedAt, tags);
        this.projectId = projectId;
        this.status = status;
        this.initiator = initiator;
        this.origin = origin;
        this.branch = branch;
    }

    public static <T> T fromLine(String line) {
        return JsonParser.parse(line, TypeFactory.defaultInstance().constructType(Scan.class));
    }

    public static <T> List<T> listFromLine(String line) {
        return JsonParser.parse(line, TypeFactory.defaultInstance().constructCollectionType(List.class, Scan.class));
    }
}
