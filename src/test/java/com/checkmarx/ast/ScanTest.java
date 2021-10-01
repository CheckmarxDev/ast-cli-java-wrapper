package com.checkmarx.ast;

import com.checkmarx.ast.scan.Scan;
import com.checkmarx.ast.wrapper.CLIConstants;
import com.checkmarx.ast.wrapper.CLIOutput;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanTest extends BaseTest {


    private Map<String, String> commonParams() {
        Map<String, String> params = new HashMap<>();
        params.put(CLIConstants.PROJECT_NAME, "JavaWrapperTestCases");
        params.put(CLIConstants.SOURCE, ".");
        params.put(CLIConstants.FILE_FILTER, "*.java");
        params.put(CLIConstants.SAST_PRESET_NAME, "Checkmarx Default");
        return params;
    }

    @Test
    public void testScanShow() throws Exception {
        CLIOutput<List<Scan>> scanList = wrapper.scanList();
        Assert.assertEquals(0, scanList.getExitCode());
        CLIOutput<Scan> cliOutput = wrapper.scanShow(scanList.getOutput().get(0).getID());
        Assert.assertEquals(0, cliOutput.getExitCode());
        Assert.assertEquals(scanList.getOutput().get(0).getID(), cliOutput.getOutput().getID());
    }

    @Test
    public void testScanList() throws Exception {
        CLIOutput<List<Scan>> cliOutput = wrapper.scanList("limit=10");
        Assert.assertEquals(0, cliOutput.getExitCode());
        Assert.assertTrue(cliOutput.getOutput().size() <= 10);
    }

    @Test
    public void testScanCreate() throws Exception {
        Map<String, String> params = commonParams();
        CLIOutput<Scan> cliOutput = wrapper.scanCreate(params);
        Assert.assertEquals(0, cliOutput.getExitCode());
        Assert.assertEquals("Completed", wrapper.scanShow(cliOutput.getOutput().getID()).getOutput().getStatus());
    }

    @Test
    public void testScanCreateWithBranchName() throws Exception {
        Map<String, String> params = commonParams();
        params.put(CLIConstants.BRANCH, "test");
        CLIOutput<Scan> cliOutput = wrapper.scanCreate(params);
        Assert.assertEquals(0, cliOutput.getExitCode());
        Assert.assertEquals("Completed", wrapper.scanShow(cliOutput.getOutput().getID()).getOutput().getStatus());
    }

    @Test
    public void testScanCreateWrongPreset() throws Exception {
        Map<String, String> params = commonParams();
        params.put(CLIConstants.SAST_PRESET_NAME, "InvalidPreset");
        CLIOutput<Scan> cliOutput = wrapper.scanCreate(params);
        Assert.assertEquals(1, cliOutput.getExitCode());
    }
}
