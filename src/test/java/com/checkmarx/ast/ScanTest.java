package com.checkmarx.ast;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanTest extends BaseTest {

    @Test
    public void testScanShow() throws IOException, InterruptedException {
        String scanId = "cebb02f1-63b8-44f7-a2fe-3c67bd48afe4";
        CLIOutput<Scan> scanCLIOutput = wrapper.scanShow(scanId);
        Assert.assertEquals(0, scanCLIOutput.getExitCode());
        Assert.assertEquals(scanId, scanCLIOutput.getOutput().getID());
    }

    @Test
    public void testScanList() throws IOException, InterruptedException {
        CLIOutput<List<Scan>> scanCLIOutput = wrapper.scanList("limit=10");
        Assert.assertEquals(0, scanCLIOutput.getExitCode());
        Assert.assertTrue(scanCLIOutput.getOutput().size() <= 10);
    }

    @Test
    public void testScanCreate() throws IOException, InterruptedException {
        Map<String, String> params = new HashMap<>();
        params.put("--project-name", "JavaWrapperTestCases");
        params.put("--scan-types", "sast");
        params.put("-s", ".");
        params.put("--file-filter", "*.java");
        params.put("--sast-preset-name", "Checkmarx Default");
        CLIOutput<Scan> scanCLIOutput = wrapper.scanCreate(params, "");
        Assert.assertEquals(0, scanCLIOutput.getExitCode());
        Assert.assertEquals("Completed", wrapper.scanShow(scanCLIOutput.getOutput().getID()).getOutput().getStatus());
    }
}
