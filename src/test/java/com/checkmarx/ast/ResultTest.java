package com.checkmarx.ast;

import com.checkmarx.ast.codebashing.CodeBashing;
import com.checkmarx.ast.results.ReportFormat;
import com.checkmarx.ast.results.Results;
import com.checkmarx.ast.results.ResultsSummary;
import com.checkmarx.ast.results.result.Data;
import com.checkmarx.ast.results.result.Node;
import com.checkmarx.ast.results.result.Result;
import com.checkmarx.ast.scan.Scan;
import com.checkmarx.ast.wrapper.CxConstants;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

class ResultTest extends BaseTest {
    private static String CWE_ID = "79";
    private static String LANGUAGE = "PHP";
    private static String QUERY_NAME = "Reflected XSS All Clients";
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

    @Test()
    void testResultsCodeBashing() throws Exception {
        List<CodeBashing> codeBashingList = wrapper.codeBashingList(CWE_ID,LANGUAGE,QUERY_NAME);
        Assertions.assertTrue(codeBashingList.size() > 0);
        String path = codeBashingList.get(0).getPath();
        Assertions.assertTrue(path.length() > 0);

    @Test
    void testResultsBflJSON() throws Exception {

        UUID scanId = UUID.fromString(CX_SCAN_ID);
        Results results = wrapper.results(scanId);
        Result result = results.getResults().stream().filter(res -> res.getType().equalsIgnoreCase(CxConstants.SAST)).findFirst().get();
        Data data = result.getData();
        String queryId = data.getQueryId();
        List<Node> resultsBfl = wrapper.getResultsBfl(scanId, queryId);
        Assertions.assertTrue(resultsBfl.size() > 0);
        Assertions.assertTrue(StringUtils.isNotEmpty(resultsBfl.get(0).getFileName()));
        Assertions.assertTrue(StringUtils.isNotEmpty(resultsBfl.get(0).getMethod()));

    }

    @Test
    void testResultsBflWithInvalidQueryId() throws Exception {

        UUID scanId = UUID.fromString(CX_SCAN_ID);
        String queryId = "0000";
        List<Node> resultsBfl = wrapper.getResultsBfl(scanId, queryId);
        Assertions.assertTrue(resultsBfl.isEmpty());

    }
}
