package com.checkmarx.ast.engine;

import com.checkmarx.ast.scan.Scan;
import com.checkmarx.ast.utils.JsonParser;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.List;

@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Engine {

    String engineId;
    String engineName;
    String apiURL;
    String apiName;
    String description;

    @JsonCreator
    public Engine(@JsonProperty("EngineID") String engineId, @JsonProperty("EngineName") String engineName,
        @JsonProperty("ApiURL") String apiURL, @JsonProperty("ApiName") String apiName, @JsonProperty("Description") String description){
        this.engineId=engineId;
        this.engineName=engineName;
        this.apiURL=apiURL;
        this.apiName=apiName;
        this.description=description;
    }


    public static <T> T fromLine(String line) {
        return JsonParser.parse(line, TypeFactory.defaultInstance().constructType(Engine.class));
    }

    public static <T> List<T> listFromLine(String line) {
        return JsonParser.parse(line, TypeFactory.defaultInstance().constructCollectionType(List.class, Engine.class));
    }


}
