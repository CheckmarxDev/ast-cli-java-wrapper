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

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.*;

public class CxAuthTest {
    private Logger log = LoggerFactory.getLogger(CxAuthTest.class.getName());
    CxAuth auth = null;
    CxScanConfig config = new CxScanConfig();
    List<CxScan> scanList = new ArrayList<CxScan>();
    Map<CxParamType,String> params = new HashMap<>();
    

    @Before
    public void init() {
    if(System.getenv("CX_CLIENT_ID") != null) {
        config.setClient_id(System.getenv("CX_CLIENT_ID"));
    }
    if(System.getenv("CX_CLIENT_SECRET") != null) {
        config.setClient_id(System.getenv("CX_CLIENT_SECRET"));
    }
    if(System.getenv("CX_APIKEY") != null) {
        config.setClient_id(System.getenv("CX_APIKEY"));
    }
    params.put(CxParamType.PROJECT_NAME,"TestCaseWrapper");
    params.put(CxParamType.SCAN_TYPES,"sast");
    params.put(CxParamType.D,".");    
    params.put(CxParamType.FILTER,"*.java");
        try {
             auth = new CxAuth(config,log);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void cxScanShow() {
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
    public void cxAstScanList() {
        init();
        if(auth != null) {
            try {
                scanList = auth.cxAstScanList();
                assertTrue(scanList.size()>0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void cxScanCreationWrongPreset() {
        init();
        if(auth != null) {
            
            try {
                params.put(CxParamType.SAST_PRESET_NAME,"Checkmarx Default Jay");
                CxScan scanResult = auth.cxScanCreate(params);
                assertTrue(auth.cxScanShow(scanResult.getID()).getStatus().equalsIgnoreCase("failed"));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void cxScanCreationSuccess() {
        init();
        if(auth != null) {
            
            try {
                params.put(CxParamType.SAST_PRESET_NAME,"Checkmarx Default");
                CxScan scanResult = auth.cxScanCreate(params);
                assertTrue(auth.cxScanShow(scanResult.getID()).getStatus().equalsIgnoreCase("completed"));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void cxScanCreationAuthenticationFailed() {
        init();
        if(auth != null) {
            
            try {
               // params.put(CxParamType.,"Checkmarx Default");
                CxScan scanResult = auth.cxScanCreate(params);
                assertTrue(auth.cxScanShow(scanResult.getID()).getStatus().equalsIgnoreCase("completed"));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    
}