package com.checkmarx.ast;

import com.checkmarx.ast.project.Project;
import com.checkmarx.ast.scan.Scan;
import com.checkmarx.ast.wrapper.CxConstants;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProjectTest extends BaseTest {

    @Test
    public void testProjectShow() throws Exception {
        List<Project> projectList = wrapper.projectList();
        Assert.assertTrue(projectList.size() > 0);
        Project project = wrapper.projectShow(UUID.fromString(projectList.get(0).getID()));
        Assert.assertEquals(projectList.get(0).getID(), project.getID());
    }

    @Test
    public void testProjectList() throws Exception {
        List<Project> projectList = wrapper.projectList("limit=10");
        Assert.assertTrue(projectList.size() <= 10);
    }

    @Test
    public void testProjectBranches() throws Exception {
        Map<String, String> params = commonParams();
        params.put(CxConstants.BRANCH, "test");
        Scan scan = wrapper.scanCreate(params);
        List<String> branches = wrapper.projectBranches(UUID.fromString(scan.getProjectID()), "");
        Assert.assertTrue(branches.size() >= 1);
        Assert.assertTrue(branches.contains("test"));
    }
}
