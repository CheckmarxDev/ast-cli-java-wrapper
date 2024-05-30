package com.checkmarx.ast;

import com.checkmarx.ast.ScanResult.ScanResult;
import com.checkmarx.ast.kicsRealtimeResults.KicsRealtimeResults;
import com.checkmarx.ast.scan.Scan;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    void testScanVorpalSuccessfulResponse() throws Exception {
        ScanResult scanResult = wrapper.ScanVorpal("src/test/resources/csharp-file.cs", true);
        Assertions.assertNotNull(scanResult.getRequestId());
        Assertions.assertTrue(scanResult.isStatus());
        Assertions.assertNull(scanResult.getError());
    }

    @Test
    void testScanVorpalFailureResponse() throws Exception {
        ScanResult scanResult = wrapper.ScanVorpal("src/test/resources/csharp-file.cs", false);
        Assertions.assertEquals("1111", scanResult.getRequestId());
        Assertions.assertFalse(scanResult.isStatus());
        Assertions.assertNotNull(scanResult.getError());
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
