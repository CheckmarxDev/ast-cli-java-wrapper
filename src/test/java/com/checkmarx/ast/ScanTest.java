package com.checkmarx.ast;

import com.checkmarx.ast.ScanResult.Error;
import com.checkmarx.ast.ScanResult.ScanDetail;
import com.checkmarx.ast.ScanResult.ScanResult;
import com.checkmarx.ast.kicsRealtimeResults.KicsRealtimeResults;
import com.checkmarx.ast.scan.Scan;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

class ScanTest extends BaseTest {

    @Test
    void testScanShow() throws Exception {
        List<Scan> scanList = wrapper.scanList();
        Assertions.assertTrue(scanList.size() > 0);
        Scan scan = wrapper.scanShow(UUID.fromString(scanList.get(0).getId()));
        Assertions.assertEquals(scanList.get(0).getId(), scan.getId());
    }

    @Test
    void testScanVorpal_WhenFileWithVulnerabilitiesIsSent_ReturnSuccessfulResponseWithCorrectValues() throws Exception {
        ScanResult scanResult = wrapper.ScanVorpal("src/test/resources/python-vul-file.py", true);
        Assertions.assertNotNull(scanResult.getRequestId());
        Assertions.assertTrue(scanResult.isStatus());
        Assertions.assertEquals(2, scanResult.getScanDetails().size());
        Assertions.assertNull(scanResult.getError());
        ScanDetail firstScanDetails = scanResult.getScanDetails().get(0);
        Assertions.assertEquals(37, firstScanDetails.getLine());
        Assertions.assertEquals("Stored XSS", firstScanDetails.getQueryName());
        Assertions.assertEquals("High", firstScanDetails.getSeverity());
        Assertions.assertNotNull(firstScanDetails.getRemediation());
        Assertions.assertNotNull(firstScanDetails.getDescription());
        ScanDetail secondScanDetails = scanResult.getScanDetails().get(1);
        Assertions.assertEquals(76, secondScanDetails.getLine());
        Assertions.assertEquals("Missing HSTS Header", secondScanDetails.getQueryName());
        Assertions.assertEquals("Medium", secondScanDetails.getSeverity());
        Assertions.assertNotNull(secondScanDetails.getRemediation());
        Assertions.assertNotNull(secondScanDetails.getDescription());
    }

    @Test
    void testScanVorpal_WhenFileWithoutVulnerabilitiesIsSent_ReturnSuccessfulResponseWithCorrectValues() throws Exception {
        ScanResult scanResult = wrapper.ScanVorpal("src/test/resources/csharp-no-vul.cs", true);
        Assertions.assertNotNull(scanResult.getRequestId());
        Assertions.assertTrue(scanResult.isStatus());
        Assertions.assertEquals(0, scanResult.getScanDetails().size());
        Assertions.assertNull(scanResult.getError());
    }

    @Test
    void testScanVorpal_WhenInvalidRequestIsSent_ReturnInternalErrorFailure() throws Exception {
        ScanResult scanResult = wrapper.ScanVorpal("src/test/resources/python-vul-file.py", false);
        Assertions.assertEquals("some-request-id", scanResult.getRequestId());
        Assertions.assertFalse(scanResult.isStatus());
        Assertions.assertNull(scanResult.getScanDetails());
        Error error = scanResult.getError();
        Assertions.assertNotNull(error);
        Assertions.assertEquals("An internal error occurred.", error.description);
        Assertions.assertEquals(2, error.code);
    }

    @Test
    void testScanList() throws Exception {
        List<Scan> cxOutput = wrapper.scanList("limit=10");
        Assertions.assertTrue(cxOutput.size() <= 10);
    }

    @Test
    void testScanCreate() throws Exception {
        Map<String, String> params = commonParams();
        Scan scan = wrapper.scanCreate(params);
        Assertions.assertEquals("Completed", wrapper.scanShow(UUID.fromString(scan.getId())).getStatus());
    }

    @Test
    void testScanCancel() throws Exception {
        Map<String, String> params = commonParams();
        Scan scan = wrapper.scanCreate(params, "--async --sast-incremental");
        Assertions.assertDoesNotThrow(() -> wrapper.scanCancel(scan.getId()));
    }

    @Test
    void testKicsRealtimeScan() throws Exception {
        KicsRealtimeResults scan = wrapper.kicsRealtimeScan("target/test-classes/Dockerfile","","v");
        Assertions.assertTrue(scan.getResults().size() >= 1);
    }

}
