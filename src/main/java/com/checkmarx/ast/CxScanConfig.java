package com.checkmarx.ast;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter

public class CxScanConfig implements Serializable {

    private String baseuri;
    private String pathToExecutable;
    private CxAuthType authType;
    private String clientId;
    private String clientSecret;
    private String apikey;

}
