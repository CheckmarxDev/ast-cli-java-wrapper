package com.checkmarx.ast;

import com.checkmarx.ast.project.Project;
import com.checkmarx.ast.wrapper.CxOutput;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

public class ProjectTest extends BaseTest {

    @Test
    public void testProjectShow() throws Exception {
        CxOutput<List<Project>> projectListOutput = wrapper.projectList();
        Assert.assertEquals(0, projectListOutput.getExitCode());
        Assert.assertTrue(projectListOutput.getOutput().size() > 0);
        CxOutput<Project> cxOutput = wrapper.projectShow(UUID.fromString(projectListOutput.getOutput().get(0).getID()));
        Assert.assertEquals(0, cxOutput.getExitCode());
        Assert.assertEquals(projectListOutput.getOutput().get(0).getID(), cxOutput.getOutput().getID());
    }

    @Test
    public void testProjectList() throws Exception {
        CxOutput<List<Project>> cxOutput = wrapper.projectList("limit=10");
        Assert.assertEquals(0, cxOutput.getExitCode());
        Assert.assertTrue(cxOutput.getOutput().size() <= 10);
    }
}
