package com.checkmarx.ast.scans;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
public class CxScanConfig implements Serializable {

    private String baseUri;
    private String baseAuthUri;
    private String tenant;
    private String clientId;
    private String clientSecret;
    private String apiKey;
    private String pathToExecutable;
    private String additionalParameters;
}
