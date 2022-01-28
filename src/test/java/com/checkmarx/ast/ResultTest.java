package com.checkmarx.ast;

import com.checkmarx.ast.results.ReportFormat;
import com.checkmarx.ast.results.Results;
import com.checkmarx.ast.results.ResultsSummary;
import com.checkmarx.ast.scan.Scan;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

class ResultTest extends BaseTest {

    @Test
    void testResultsHTML() throws Exception {
        List<Scan> scanList = wrapper.scanList();
        Assertions.assertTrue(scanList.size() > 0);
        String scanId = scanList.get(0).getId();
        String results = wrapper.results(UUID.fromString(scanId), ReportFormat.summaryHTML);
        Assertions.assertTrue(results.length() > 0);
    }

    @Test
    void testResultsJSON() throws Exception {
        List<Scan> scanList = wrapper.scanList();
        Assertions.assertTrue(scanList.size() > 0);
        String scanId = scanList.get(0).getId();
        String results = wrapper.results(UUID.fromString(scanId), ReportFormat.json);
        Assertions.assertTrue(results.length() > 0);
    }

    @Test
    void testResultsSummaryJSON() throws Exception {
        List<Scan> scanList = wrapper.scanList();
        Assertions.assertTrue(scanList.size() > 0);
        String scanId = scanList.get(0).getId();
        ResultsSummary results = wrapper.resultsSummary(UUID.fromString(scanId));
        Assertions.assertNotNull(results.getScanId());
    }

    @Test()
    void testResultsStructure() throws Exception {
        List<Scan> scanList = wrapper.scanList();
        Assertions.assertTrue(scanList.size() > 0);
        String scanId = scanList.get(0).getId();
        Results results = wrapper.results(UUID.fromString(scanId));
        results.getResults().stream().filter(result -> "sast".equalsIgnoreCase(result.getType())).findFirst();
        Assertions.assertEquals(results.getTotalCount(), results.getResults().size());
    }
}
