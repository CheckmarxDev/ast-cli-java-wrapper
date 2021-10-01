package com.checkmarx.ast;

import com.checkmarx.ast.scan.Scan;
import com.checkmarx.ast.wrapper.CxConstants;
import com.checkmarx.ast.wrapper.CxOutput;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScanTest extends BaseTest {


    private Map<String, String> commonParams() {
        Map<String, String> params = new HashMap<>();
        params.put(CxConstants.PROJECT_NAME, "JavaWrapperTestCases");
        params.put(CxConstants.SOURCE, ".");
        params.put(CxConstants.FILE_FILTER, "*.java");
        params.put(CxConstants.SAST_PRESET_NAME, "Checkmarx Default");
        return params;
    }

    @Test
    public void testScanShow() throws Exception {
        CxOutput<List<Scan>> scanList = wrapper.scanList();
        Assert.assertEquals(0, scanList.getExitCode());
        CxOutput<Scan> cxOutput = wrapper.scanShow(UUID.fromString(scanList.getOutput().get(0).getID()));
        Assert.assertEquals(0, cxOutput.getExitCode());
        Assert.assertEquals(scanList.getOutput().get(0).getID(), cxOutput.getOutput().getID());
    }

    @Test
    public void testScanList() throws Exception {
        CxOutput<List<Scan>> cxOutput = wrapper.scanList("limit=10");
        Assert.assertEquals(0, cxOutput.getExitCode());
        Assert.assertTrue(cxOutput.getOutput().size() <= 10);
    }

    @Test
    public void testScanCreate() throws Exception {
        Map<String, String> params = commonParams();
        CxOutput<Scan> cxOutput = wrapper.scanCreate(params);
        Assert.assertEquals(0, cxOutput.getExitCode());
        Assert.assertEquals("Completed",
                            wrapper.scanShow(UUID.fromString(cxOutput.getOutput().getID())).getOutput().getStatus());
    }

    @Test
    public void testScanCreateWithBranchName() throws Exception {
        Map<String, String> params = commonParams();
        params.put(CxConstants.BRANCH, "test");
        CxOutput<Scan> cxOutput = wrapper.scanCreate(params);
        Assert.assertEquals(0, cxOutput.getExitCode());
        Assert.assertEquals("Completed",
                            wrapper.scanShow(UUID.fromString(cxOutput.getOutput().getID())).getOutput().getStatus());
    }

    @Test
    public void testScanCreateWrongPreset() throws Exception {
        Map<String, String> params = commonParams();
        params.put(CxConstants.SAST_PRESET_NAME, "InvalidPreset");
        CxOutput<Scan> cxOutput = wrapper.scanCreate(params);
        Assert.assertEquals(1, cxOutput.getExitCode());
    }
}
