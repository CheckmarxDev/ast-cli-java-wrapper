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
    private String client_id;
    private String client_secret;
    private String apikey;


}
