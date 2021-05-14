package com.checkmarx.ast;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class CxAuthTest {
    private Logger log = LoggerFactory.getLogger(CxAuthTest.class.getName());
    CxAuth auth = null;
    CxScanConfig config = new CxScanConfig();

    @Before
    public void init() {
    config.setBaseuri("https://prod1.ast-cloud.com");
    config.setClient_id("ast-client");
    config.setClient_secret("64a75d89-a7de-4eea-be38-9ca5d0f66a50");
    Map<CxParamType,String> params = new HashMap<>();
    params.put(CxParamType.PROJECT_NAME,"TestProj");

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

    }

    @Test
    public void cxAstScanList() {
        init();
        if(auth != null) {
            try {
                auth.cxAstScanList();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void cxScanCreate() {
    }
}