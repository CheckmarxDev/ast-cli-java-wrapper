package com.checkmarx.ast;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter

public class CxScanConfig implements Serializable {

    private String baseuri;
    private String pathToExecutable;
    private CxAuthType authType;
    private String key;
    private String secret;
    private String token;


}
