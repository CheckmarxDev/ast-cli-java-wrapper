package com.checkmarx.ast;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class CxAuthTest {
    private static final Logger log = LoggerFactory.getLogger(CxAuthTest.class.getName());

    private static final int VALID_RETURN_CODE = 0;
    private static final String FAILED = "failed";
    private static final String COMPLETED = "completed";

    private CxAuth auth;

    @Before
    public void init() throws IOException, URISyntaxException {
        log.info("Init test");

        Map<String, String> environmentVariables = System.getenv();
        CxScanConfig config = new CxScanConfig();
        config.setClientId(environmentVariables.getOrDefault("CX_CLIENT_ID", null));
        config.setClientSecret(environmentVariables.getOrDefault("CX_CLIENT_SECRET", null));
        config.setApiKey(environmentVariables.getOrDefault("CX_APIKEY", null));
        config.setBaseUri(environmentVariables.getOrDefault("CX_BASE_URI", null));
        config.setBaseAuthUri(environmentVariables.getOrDefault("CX_BASE_AUTH_URI", null));
        config.setTenant(environmentVariables.getOrDefault("CX_TENANT", null));
        config.setPathToExecutable(environmentVariables.getOrDefault("PATH_TO_EXECUTABLE", null));

        auth = new CxAuth(config, log);
    }

    @NotNull
    private Map<CxParamType, String> createParams() {
        Map<CxParamType, String> params = new HashMap<>();
        params.put(CxParamType.PROJECT_NAME, "TestCaseWrapper");
        params.put(CxParamType.SCAN_TYPES, "sast");
        params.put(CxParamType.S, ".");
        params.put(CxParamType.FILTER, "*.java");

        return params;
    }

    @Test
    public void cxScanShow() throws InterruptedException, IOException {
        CxCommandOutput scanList = auth.cxAstScanList(); //scan ID
        assertNotNull(scanList.getScanObjectList().get(0));
    }

    @Test
    public void cxAstAuthValidate() throws IOException, InterruptedException {
        Integer validate = auth.cxAuthValidate();
        assertEquals(VALID_RETURN_CODE, validate.intValue());
    }

    @Test
    public void cxAstScanList() throws IOException, InterruptedException {
        CxCommandOutput scanList = auth.cxAstScanList();
        assertTrue(scanList.getScanObjectList().size() > 0);
    }

    @Test
    public void cxScanCreationWithBranchName() throws InterruptedException, IOException {
        Map<CxParamType, String> params = createParams();
        params.put(CxParamType.BRANCH, "test");

        CxCommandOutput scanResult = auth.cxScanCreate(params);
        assertTrue(auth.cxScanShow(scanResult.getScanObjectList().get(0).getID()).getScanObjectList().get(0).getStatus().equalsIgnoreCase(COMPLETED));
    }

    @Test
    public void cxScanCreationWrongPreset() throws InterruptedException, IOException {
        Map<CxParamType, String> params = createParams();
        params.put(CxParamType.SAST_PRESET_NAME, "Checkmarx Default Jay");

        CxCommandOutput scanResult = auth.cxScanCreate(params);

        assertTrue(auth.cxScanShow(scanResult.getScanObjectList().get(0).getID()).getScanObjectList().get(0).getStatus().equalsIgnoreCase(FAILED));
    }


    @Test
    public void cxScanCreationSuccess() throws InterruptedException, IOException {
        Map<CxParamType, String> params = createParams();
        params.put(CxParamType.SAST_PRESET_NAME, "Checkmarx Default");
        //params.put(CxParamType.ADDITIONAL_PARAMETERS,"--nowait");

        CxCommandOutput scanResult = auth.cxScanCreate(params);
        assertTrue(auth.cxScanShow(scanResult.getScanObjectList().get(0).getID()).getScanObjectList().get(0).getStatus().equalsIgnoreCase(COMPLETED));
    }
}