package com.checkmarx.ast.learnMore;

import com.checkmarx.ast.codebashing.CodeBashing;
import com.checkmarx.ast.remediation.KicsRemediation;
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
import java.util.List;

@Value
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

public class LearnMore {

    String queryId;
    String queryName;
    String queryDescriptionId;
    String resultDescription;
    String risk;
    String cause;
    String generalRecommendations;
    List<Sample> samples;

    @JsonCreator
    public LearnMore(@JsonProperty("queryID") String queryId, @JsonProperty("queryName") String queryName,@JsonProperty("queryDescriptionID") String queryDescriptionId, @JsonProperty("resultDescription") String resultDescription,@JsonProperty("risk") String risk,@JsonProperty("cause") String cause,@JsonProperty("generalRecommendations") String generalRecommendations,@JsonProperty("samples") List<Sample> samples) {
        this.queryId = queryId;
        this.queryName = queryName;
        this.queryDescriptionId = queryDescriptionId;
        this.resultDescription = resultDescription;
        this.risk = risk;
        this.cause = cause;
        this.generalRecommendations = generalRecommendations;
        this.samples = samples;
    }

    public static <T> List<T> listFromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructCollectionType(List.class, LearnMore.class));
    }

    public static <T> T fromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructType(LearnMore.class));
    }

    private static <T> T parse(String line, JavaType type) {
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
