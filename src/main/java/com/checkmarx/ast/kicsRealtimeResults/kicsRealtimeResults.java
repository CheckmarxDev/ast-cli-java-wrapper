package com.checkmarx.ast.kicsRealtimeResults;

import com.checkmarx.ast.kicsRealtimeResults.ast.kicsRealtimeResult.KicsResult;
import com.checkmarx.ast.kicsRealtimeResults.ast.kicsRealtimeResult.KicsSummary;

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
public class kicsRealtimeResults {

    int totalCount;
    String version;
    List<KicsResult> results;
    KicsSummary kicsSummary;

    @JsonCreator
    public kicsRealtimeResults(@JsonProperty("total_counter") int totalCount, @JsonProperty("queries") List<KicsResult> results,@JsonProperty("kics_version") String version, @JsonProperty("severity_counters") KicsSummary kicsSummary) {
        this.totalCount = totalCount;
        this.version = version;
        this.results = results;
        this.kicsSummary = kicsSummary;
    }
    public static <T> T fromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructType(kicsRealtimeResults.class));
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
