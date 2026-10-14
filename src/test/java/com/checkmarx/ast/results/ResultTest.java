package com.checkmarx.ast.results;

import com.checkmarx.ast.BaseTest;
import com.checkmarx.ast.codebashing.CodeBashing;
import com.checkmarx.ast.results.result.Data;
import com.checkmarx.ast.results.result.Node;
import com.checkmarx.ast.results.result.Result;
import com.checkmarx.ast.scan.Scan;
import com.checkmarx.ast.wrapper.CxConstants;
import com.checkmarx.ast.wrapper.CxException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class ResultTest extends BaseTest {
    private static String CWE_ID = "79";
    private static String LANGUAGE = "PHP";
    private static String QUERY_NAME = "Reflected XSS All Clients";
    private static final int CODEBASHING_LICENSE_NOT_FOUND_EXIT_CODE = 3;
    private static final int CODEBASHING_LESSON_NOT_FOUND_EXIT_CODE = 4;

    @Test
    void testResultsHTML() throws Exception {
        List<Scan> scanList = wrapper.scanList("statuses=Completed");
        Assumptions.assumeTrue(scanList != null && !scanList.isEmpty(), "No completed scans available");
        String scanId = scanList.get(0).getId();
        String results = wrapper.results(UUID.fromString(scanId), ReportFormat.summaryHTML);
        Assertions.assertTrue(results.length() > 0);
    }

    @Test
    void testResultsJSON() throws Exception {
        List<Scan> scanList = wrapper.scanList("statuses=Completed");
        Assumptions.assumeTrue(scanList != null && !scanList.isEmpty(), "No completed scans available");
        String scanId = scanList.get(0).getId();
        String results = wrapper.results(UUID.fromString(scanId), ReportFormat.json, "java-wrapper");
        Assertions.assertTrue(results.length() > 0);
    }

    @Test
    void testResultsSummaryJSON() throws Exception {
        List<Scan> scanList = wrapper.scanList("statuses=Completed");
        Assumptions.assumeTrue(scanList != null && scanList.size() > 0, "No completed scans available");
        String scanId = scanList.get(0).getId();
        ResultsSummary results = wrapper.resultsSummary(UUID.fromString(scanId));
        Assertions.assertNotNull(results.getScanId());
    }

    @Test()
    void testResultsStructure() throws Exception {
        List<Scan> scanList = wrapper.scanList("statuses=Completed");
        Assumptions.assumeTrue(scanList != null && scanList.size() > 0, "No completed scans available");
        for (Scan scan : scanList) {
            Results results = wrapper.results(UUID.fromString(scan.getId()));
            if (results != null && results.getResults() != null) {
                Assertions.assertEquals(results.getTotalCount(), results.getResults().size());
                return;
            }
        }
        Assumptions.abort("No results found in any completed scan");
    }

    @Test()
    void testResultsCodeBashing() throws Exception {
        List<CodeBashing> codeBashingList;
        try {
            codeBashingList = wrapper.codeBashingList(CWE_ID, LANGUAGE, QUERY_NAME);
        } catch (CxException e) {
            // The CLI exits with 3 (no CodeBashing license) or 4 (no lesson mapped, "No codebashing link available").
            // Both depend on the tenant/backend mapping, not on the wrapper.
            Assumptions.assumeFalse(e.getExitCode() == CODEBASHING_LICENSE_NOT_FOUND_EXIT_CODE
                    || e.getExitCode() == CODEBASHING_LESSON_NOT_FOUND_EXIT_CODE, e.getMessage());
            throw e;
        }
        Assertions.assertTrue(codeBashingList.size() > 0);
        String path = codeBashingList.get(0).getPath();
        Assertions.assertTrue(path.length() > 0);
    }

    @Test
    void testResultsBflJSON() throws Exception {
        try {
            Map<String, String> params = commonParams();
            Scan scan = wrapper.scanCreate(params);
            UUID scanId = UUID.fromString(scan.getId());

            Assertions.assertEquals("Completed", wrapper.scanShow(scanId).getStatus());

            Results results = wrapper.results(scanId);
            Result result = results.getResults().stream().filter(res -> res.getType().equalsIgnoreCase(CxConstants.SAST)).findFirst().get();
            Data data = result.getData();
            String queryId = data.getQueryId();
            int bflNodeIndex = wrapper.getResultsBfl(scanId, queryId, data.getNodes());
            Assertions.assertTrue(bflNodeIndex == -1 || bflNodeIndex >= 0);

            String queryIdInvalid = "0000";
            int bflNodeIndexInvalid = wrapper.getResultsBfl(scanId, queryIdInvalid, new ArrayList<Node>());
            Assertions.assertEquals(-1, bflNodeIndexInvalid);
        } catch (com.checkmarx.ast.wrapper.CxException e) {
            if (e.getMessage().contains("already exists")) {
                Assumptions.abort("Project already exists (test isolation issue): " + e.getMessage());
            }
            throw e;
        }
    }
}
