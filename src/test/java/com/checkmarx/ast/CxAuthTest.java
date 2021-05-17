package com.checkmarx.ast;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.junit.Assert.*;

public class CxAuthTest {
    CxAuth auth = null;
    CxScanConfig config = new CxScanConfig();
    List<CxScan> scanList = new ArrayList<CxScan>();
    Map<CxParamType,String> params = new HashMap<>();
    Map<String,String> environmentVariables = System.getenv();

    @Before
    public void init() throws InterruptedException,IOException,URISyntaxException{     
    if(environmentVariables.containsKey("CX_CLIENT_ID")) {
        config.setClient_id(environmentVariables.get("CX_CLIENT_ID"));
    }
    if( environmentVariables.containsKey("CX_CLIENT_SECRET")) {
        config.setClient_secret(environmentVariables.get("CX_CLIENT_SECRET"));
    }
    if(environmentVariables.containsKey("CX_APIKEY")) {
        config.setApikey(environmentVariables.get("CX_APIKEY"));
    }
    if(environmentVariables.containsKey("CX_BASE_URI")) {
        config.setBaseuri(environmentVariables.get("CX_BASE_URI"));
    }
    if(environmentVariables.containsKey("PATH_TO_EXECUTABLE")) {
        config.setPathToExecutable(environmentVariables.get("PATH_TO_EXECUTABLE"));
    }
    params.put(CxParamType.PROJECT_NAME,"TestCaseWrapper");
    params.put(CxParamType.SCAN_TYPES,"sast");
    params.put(CxParamType.D,".");    
    params.put(CxParamType.FILTER,"*.java");
        
    }

    @Test
    public void cxScanShow() throws InterruptedException, IOException, URISyntaxException {
        init();
        if(auth != null && scanList.size()>0) {
            for(int index=0; index < 5; index++) {
                assertTrue(scanList.get(index) instanceof CxScan);
            }
        }  
        else {
            cxAstScanList();
            cxScanShow();
        }

    }

    @Test
    public void cxAstScanList() throws IOException, InterruptedException, URISyntaxException {
        init();
        if(auth != null) {
            scanList = auth.cxAstScanList();
            assertTrue(scanList.size()>0);
            
        }
    }

    @Test
    public void cxScanCreationWrongPreset() throws InterruptedException, IOException, URISyntaxException {
        init();
        if(auth != null) {
            params.put(CxParamType.SAST_PRESET_NAME,"Checkmarx Default Jay");
            CxScan scanResult = auth.cxScanCreate(params);
            assertTrue(auth.cxScanShow(scanResult.getID()).getStatus().equalsIgnoreCase("failed"));
            
        }
    }

    @Test
    public void cxScanCreationSuccess() throws InterruptedException, IOException, URISyntaxException {
        init();
        if(auth != null) {          
            params.put(CxParamType.SAST_PRESET_NAME,"Checkmarx Default");
            CxScan scanResult = auth.cxScanCreate(params);
            assertTrue(auth.cxScanShow(scanResult.getID()).getStatus().equalsIgnoreCase("completed"));
            
        }
    }
    
}