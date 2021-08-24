package com.checkmarx.ast;

import com.checkmarx.ast.results.CxCommandOutput;
import com.checkmarx.ast.results.CxResultFormatType;
import com.checkmarx.ast.results.structure.CxResultOutput;
import com.checkmarx.ast.scans.CxAuth;
import com.checkmarx.ast.scans.CxParamType;
import com.checkmarx.ast.scans.CxScanConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    private Map<CxParamType, String> createParams() {
        Map<CxParamType, String> params = new HashMap<>();
        params.put(CxParamType.PROJECT_NAME, "JavaWrapperTestCases");
        params.put(CxParamType.SCAN_TYPES, "sast");
        params.put(CxParamType.S, ".");
        params.put(CxParamType.FILE_FILTER, "*.java");

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
        String status = auth.cxScanShow(scanResult.getScanObjectList().get(0).getID())
                            .getScanObjectList()
                            .get(0)
                            .getStatus();
        assertTrue(status.equalsIgnoreCase(COMPLETED));
    }

    @Test
    public void cxScanCreationWrongPreset() throws InterruptedException, IOException {
        Map<CxParamType, String> params = createParams();
        params.put(CxParamType.SAST_PRESET_NAME, "Checkmarx Default Jay");

        CxCommandOutput scanResult = auth.cxScanCreate(params);
        String status = auth.cxScanShow(scanResult.getScanObjectList().get(0).getID())
                            .getScanObjectList()
                            .get(0)
                            .getStatus();
        assertTrue(status.equalsIgnoreCase(FAILED));
    }


    @Test
    public void cxScanCreationSuccess() throws InterruptedException, IOException {
        Map<CxParamType, String> params = createParams();
        params.put(CxParamType.SAST_PRESET_NAME, "Checkmarx Default");
        //params.put(CxParamType.ADDITIONAL_PARAMETERS,"--nowait");

        CxCommandOutput scanResult = auth.cxScanCreate(params);
        assertTrue(auth.cxScanShow(scanResult.getScanObjectList().get(0).getID())
                       .getScanObjectList()
                       .get(0)
                       .getStatus()
                       .equalsIgnoreCase(COMPLETED));
    }


    @Test
    public void cxGenerateHTMLResults() throws InterruptedException, IOException {
        CxCommandOutput scanList = auth.cxAstScanList();
        String id = scanList.getScanObjectList().get(0).getID();
        String filePath = System.getProperty("user.dir") + "/index.html";
        auth.cxGetResultsSummary(id, "", filePath);
        assertTrue(new File(filePath).length() > 0);
    }

    @Test
    public void cxGetResultsSummaryString() throws InterruptedException, IOException {
        CxCommandOutput scanList = auth.cxAstScanList();
        String id = scanList.getScanObjectList().get(0).getID();
        String op = auth.cxGetResultsSummary(id, "", "");
        assertTrue(op.length() > 0);
    }

    @Test
    public void cxGetResultsListString() throws InterruptedException, IOException {
        CxCommandOutput scanList = auth.cxAstScanList();
        String id = scanList.getScanObjectList().get(0).getID();
        String op = auth.cxGetResultsList(id, String.valueOf(CxResultFormatType.JSON));
        assertTrue(op.length() > 0);
    }

    @Test
    public void cxResultsStructure() {
        String scanID = null;
        try {
            scanID = auth.cxAstScanList().getScanObjectList().get(0).getID();
        } catch (IOException | InterruptedException e) {
            fail("Failed getting a scan id");
        }
        String results = null;
        try {
            results = auth.cxGetResultsList(scanID, String.valueOf(CxResultFormatType.JSON));
        } catch (IOException e) {
            fail("Failed getting results for scan id " + scanID);
        }
        try {
            CxResultOutput resultOutput = new ObjectMapper()
                    .readerFor(CxResultOutput.class)
                    .readValue(results);
            Assert.assertEquals(resultOutput.getTotalCount(), resultOutput.getResults().size());
        } catch (IOException e) {
            fail("Failed parsing results json: " + e.getMessage());
        }
    }
}