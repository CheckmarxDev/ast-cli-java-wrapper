package com.checkmarx.ast;

import com.checkmarx.ast.project.Project;
import com.checkmarx.ast.wrapper.CLIOutput;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ProjectTest extends BaseTest {

    @Test
    public void testProjectShow() throws Exception {
        CLIOutput<List<Project>> projectListOutput = wrapper.projectList();
        Assert.assertEquals(0, projectListOutput.getExitCode());
        CLIOutput<Project> cliOutput = wrapper.projectShow(projectListOutput.getOutput().get(0).getID());
        Assert.assertEquals(0, cliOutput.getExitCode());
        Assert.assertEquals(projectListOutput.getOutput().get(0).getID(), cliOutput.getOutput().getID());
    }

    @Test
    public void testProjectList() throws Exception {
        CLIOutput<List<Project>> cliOutput = wrapper.projectList("limit=10");
        Assert.assertEquals(0, cliOutput.getExitCode());
        Assert.assertTrue(cliOutput.getOutput().size() <= 10);
    }
}
