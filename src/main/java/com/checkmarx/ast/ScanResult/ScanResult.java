package com.checkmarx.ast.ScanResult;

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
public class ScanResult extends CxBaseObject {

    String requestId;
    boolean status;
    String message;
    List<ScanDetail> scanDetails;
    Error error;

    @JsonCreator
    public ScanResult(
            @JsonProperty("request_id") String requestId,
            @JsonProperty("Status") boolean status,
            @JsonProperty("Message") String message,
            @JsonProperty("scan_details") List<ScanDetail> scanDetails,
            @JsonProperty("error") Error error)
    {
        super(null, null, null, null);
        this.requestId = requestId;
        this.status = status;
        this.message = message;
        this.scanDetails = scanDetails;
        this.error = error;
    }

    public static <T> T fromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructType(ScanResult.class));
    }
}
