package com.checkmarx.ast.ScanResult;

import com.checkmarx.ast.utils.JsonParser;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.ToString;
import lombok.Value;

import java.util.List;

@Value
@ToString(callSuper = true)
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScanResult {

    String requestId;
    boolean status;
    String message;
    List<ScanDetail> scanDetails;
    Error error;

    @JsonCreator
    public ScanResult(
            @JsonProperty("request_id") String requestId,
            @JsonProperty("status") boolean status,
            @JsonProperty("message") String message,
            @JsonProperty("scan_details") List<ScanDetail> scanDetails,
            @JsonProperty("error") Error error
    ) {
        this.requestId = requestId;
        this.status = status;
        this.message = message;
        this.scanDetails = scanDetails;
        this.error = error;
    }

    public static <T> T fromLine(String line) {
        return JsonParser.parse(line, TypeFactory.defaultInstance().constructType(ScanResult.class));
    }
}
