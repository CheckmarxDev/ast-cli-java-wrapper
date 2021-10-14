package com.checkmarx.ast.scan;

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

    String ProjectID;
    String Status;
    String Initiator;
    String Origin;
    String Branch;

    @JsonCreator
    public Scan(@JsonProperty("ID") String ID, @JsonProperty("ProjectID") String ProjectID,
                @JsonProperty("Status") String Status, @JsonProperty("CreatedAt") String CreatedAt,
                @JsonProperty("UpdatedAt") String UpdatedAt, @JsonProperty("Tags") Map<String, String> Tags,
                @JsonProperty("Initiator") String Initiator, @JsonProperty("Origin") String Origin,
                @JsonProperty("Branch") String Branch) {
        super(ID, CreatedAt, UpdatedAt, Tags);
        this.ProjectID = ProjectID;
        this.Status = Status;
        this.Initiator = Initiator;
        this.Origin = Origin;
        this.Branch = Branch;
    }

    public static <T> T fromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructType(Scan.class));
    }

    public static <T> List<T> listFromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructCollectionType(List.class, Scan.class));
    }
}
