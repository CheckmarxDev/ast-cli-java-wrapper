package com.checkmarx.ast;

import com.checkmarx.ast.project.Project;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
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
}
