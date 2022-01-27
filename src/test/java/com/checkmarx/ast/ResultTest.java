package com.checkmarx.ast;

import com.checkmarx.ast.results.ReportFormat;
import com.checkmarx.ast.results.Results;
import com.checkmarx.ast.results.ResultsSummary;
import com.checkmarx.ast.scan.Scan;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ResultTest extends BaseTest {

    @Test
    public void testResultsHTML() throws Exception {
        List<Scan> scanList = wrapper.scanList();
        Assertions.assertTrue(scanList.size() > 0);
        String scanId = scanList.get(0).getID();
        String results = wrapper.results(UUID.fromString(scanId), ReportFormat.summaryHTML);
        assertTrue(results.length() > 0);
    }

    @Test
    public void testResultsJSON() throws Exception {
        List<Scan> scanList = wrapper.scanList();
        Assertions.assertTrue(scanList.size() > 0);
        String scanId = scanList.get(0).getID();
        String results = wrapper.results(UUID.fromString(scanId), ReportFormat.json);
        assertTrue(results.length() > 0);
    }

    @Test
    public void testResultsSummaryJSON() throws Exception {
        List<Scan> scanList = wrapper.scanList();
        Assertions.assertTrue(scanList.size() > 0);
        String scanId = scanList.get(0).getID();
        ResultsSummary results = wrapper.resultsSummary(UUID.fromString(scanId));
        assertNotNull(results.getScanId());
    }

    @Test()
    public void testResultsStructure() throws Exception {
        List<Scan> scanList = wrapper.scanList();
        Assertions.assertTrue(scanList.size() > 0);
        String scanId = scanList.get(0).getID();
        Results results = wrapper.results(UUID.fromString(scanId));
        results.getResults().stream().filter(result -> "sast".equalsIgnoreCase(result.getType())).findFirst();
        Assertions.assertEquals(results.getTotalCount(), results.getResults().size());
    }
}
