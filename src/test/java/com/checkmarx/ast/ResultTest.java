package com.checkmarx.ast;

import com.checkmarx.ast.results.ReportFormat;
import com.checkmarx.ast.results.Results;
import com.checkmarx.ast.scan.Scan;
import com.checkmarx.ast.wrapper.CLIOutput;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ResultTest extends BaseTest {

    @Test
    public void testResultsHTML() throws Exception {
        CLIOutput<List<Scan>> scanList = wrapper.scanList();
        String id = scanList.getOutput().get(0).getID();
        CLIOutput<String> cliOutput = wrapper.results(id, ReportFormat.summaryHTML);
        assertTrue(cliOutput.getOutput().length() > 0);
    }

    @Test
    public void testResultsJSON() throws Exception {
        CLIOutput<List<Scan>> scanList = wrapper.scanList();
        String id = scanList.getOutput().get(0).getID();
        CLIOutput<String> cliOutput = wrapper.results(id, ReportFormat.json);
        assertTrue(cliOutput.getOutput().length() > 0);
    }

    @Test
    public void testResultsStructure() throws Exception {
        CLIOutput<List<Scan>> scanList = wrapper.scanList();
        String id = scanList.getOutput().get(0).getID();
        CLIOutput<Results> cliOutput = wrapper.results(id);
        Assert.assertEquals(cliOutput.getOutput().getTotalCount(), cliOutput.getOutput().getResults().size());
    }
}
