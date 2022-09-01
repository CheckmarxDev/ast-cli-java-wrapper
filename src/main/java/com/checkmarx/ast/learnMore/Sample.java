package com.checkmarx.ast.learnMore;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Value;

@Value
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

public class Sample {
    String progLanguage;
    String code;
    String title;

    @JsonCreator
    public Sample(@JsonProperty("progLanguage") String progLanguage, @JsonProperty("code") String code,@JsonProperty("title") String title) {
        this.progLanguage = progLanguage;
        this.code = code;
        this.title = title;
    }
}
