package com.checkmarx.ast.learnmore;

import com.fasterxml.jackson.annotation.JsonCreator;
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
public class LearnMoreDescriptions {
    String queryId;
    String queryName;
    String queryDescriptionId;
    String resultDescription;
    String risk;
    String cause;
    String generalRecommendations;
    List<Samples> samples;

    @JsonCreator
    public LearnMoreDescriptions(@JsonProperty("queryId") String queryId, @JsonProperty("queryName") String queryName,
                   @JsonProperty("queryDescriptionId") String queryDescriptionId, @JsonProperty("resultDescription") String resultDescription,
                                 @JsonProperty("risk") String risk, @JsonProperty("cause") String cause,
                                 @JsonProperty("generalRecommendations") String generalRecommendations,
                                 @JsonProperty("samples") List<Samples> samples ) {
        this.queryId = queryId;
        this.queryName = queryName;
        this.queryDescriptionId = queryDescriptionId;
        this.resultDescription = resultDescription;
        this.risk = risk;
        this.cause = cause;
        this.generalRecommendations = generalRecommendations;
        this.samples = samples;

    }

    public static <T> T fromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructType(LearnMoreDescriptions.class));
    }

    public static <T> List<T> listFromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructCollectionType(List.class, LearnMoreDescriptions.class));
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
