package com.checkmarx.ast;

import com.checkmarx.ast.asca.ScanDetail;
import com.checkmarx.ast.asca.ScanResult;
import com.checkmarx.ast.asca.Error;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ScanResultTest extends BaseTest {

    @Test
    void testScanAsca_WhenFileWithVulnerabilitiesIsSentWithAgent_ReturnSuccessfulResponseWithCorrectValues() throws Exception {
        ScanResult scanResult = wrapper.ScanAsca("src/test/resources/python-vul-file.py", true, "vscode");

        // Assertions for the scan result
        Assertions.assertNotNull(scanResult.getRequestId(), "Request ID should not be null");
        Assertions.assertTrue(scanResult.isStatus(), "Status should be true");
        Assertions.assertNull(scanResult.getError(), "Error should be null");

        // Ensure scan details are not null and contain at least one entry
        Assertions.assertNotNull(scanResult.getScanDetails(), "Scan details should not be null");
        Assertions.assertFalse(scanResult.getScanDetails().isEmpty(), "Scan details should contain at least one entry");

        // Iterate over all scan details and validate each one
        for (ScanDetail scanDetail : scanResult.getScanDetails()) {
            Assertions.assertNotNull(scanDetail.getRemediationAdvise(), "Remediation advise should not be null");
            Assertions.assertNotNull(scanDetail.getDescription(), "Description should not be null");
        }
    }

    @Test
    void testScanAsca_WhenFileWithoutVulnerabilitiesIsSent_ReturnSuccessfulResponseWithCorrectValues() throws Exception {
        ScanResult scanResult = wrapper.ScanAsca("src/test/resources/csharp-no-vul.cs", true, null);
        Assertions.assertNotNull(scanResult.getRequestId());
        Assertions.assertTrue(scanResult.isStatus());
        Assertions.assertNull(scanResult.getError());
        Assertions.assertNull(scanResult.getScanDetails()); // When no vulnerabilities are found, scan details is null
    }

    @Test
    void testScanAsca_WhenMissingFileExtension_ReturnFileExtensionIsRequiredFailure() throws Exception {
        ScanResult scanResult = wrapper.ScanAsca("CODEOWNERS", true, null);
        Assertions.assertNotNull(scanResult.getRequestId());
        Assertions.assertNotNull(scanResult.getError());
        Assertions.assertEquals("The file name must have an extension.", scanResult.getError().getDescription());
    }
}
