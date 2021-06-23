package com.checkmarx.ast;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CxScanConfig implements Serializable {
    private String baseUri;
    private String pathToExecutable;
    private CxAuthType authType;
    private String clientId;
    private String clientSecret;
    private String apiKey;
}
