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
public class Samples {
    String progLanguage;
    String code;
    String title;

    @JsonCreator
    public Samples(@JsonProperty("progLanguage") String progLanguage, @JsonProperty("code") String code,
                     @JsonProperty("title") String title) {
        this.progLanguage = progLanguage;
        this.code = code;
        this.title = title;
    }
    public static <T> T fromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructType(Samples.class));
    }

    public static <T> List<T> listFromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructCollectionType(List.class, Samples.class));
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
