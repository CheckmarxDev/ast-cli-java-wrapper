package com.checkmarx.ast;

import com.checkmarx.ast.results.ReportFormat;
import com.checkmarx.ast.results.Results;
import com.checkmarx.ast.scan.Scan;
import com.checkmarx.ast.wrapper.CxOutput;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class ResultTest extends BaseTest {

    @Test
    public void testResultsHTML() throws Exception {
        CxOutput<List<Scan>> scanList = wrapper.scanList();
        Assert.assertEquals(0, scanList.getExitCode());
        Assert.assertTrue(scanList.getOutput().size() > 0);
        String scanId = scanList.getOutput().get(0).getID();
        CxOutput<String> cxOutput = wrapper.results(UUID.fromString(scanId), ReportFormat.summaryHTML);
        assertTrue(cxOutput.getOutput().length() > 0);
    }

    @Test
    public void testResultsJSON() throws Exception {
        CxOutput<List<Scan>> scanList = wrapper.scanList();
        Assert.assertEquals(0, scanList.getExitCode());
        Assert.assertTrue(scanList.getOutput().size() > 0);
        String scanId = scanList.getOutput().get(0).getID();
        CxOutput<String> cxOutput = wrapper.results(UUID.fromString(scanId), ReportFormat.json);
        assertTrue(cxOutput.getOutput().length() > 0);
    }

    @Test
    public void testResultsStructure() throws Exception {
        CxOutput<List<Scan>> scanList = wrapper.scanList();
        Assert.assertEquals(0, scanList.getExitCode());
        Assert.assertTrue(scanList.getOutput().size() > 0);
        String scanId = scanList.getOutput().get(0).getID();
        CxOutput<Results> cxOutput = wrapper.results(UUID.fromString(scanId));
        Assert.assertEquals(cxOutput.getOutput().getTotalCount(), cxOutput.getOutput().getResults().size());
    }
}
