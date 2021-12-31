package com.checkmarx.ast;

import com.checkmarx.ast.predicate.Predicate;
import com.checkmarx.ast.project.Project;
import com.checkmarx.ast.results.Results;
import com.checkmarx.ast.results.result.Result;
import com.checkmarx.ast.scan.Scan;
import com.checkmarx.ast.wrapper.CxConstants;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.fail;

public class PredicateTest extends BaseTest {

    @Test
    public void testTriageShow() throws Exception {
        List<Scan> scanList = wrapper.scanList(String.format("statuses=Completed"));
        Scan scan = scanList.get(0);
        Assert.assertTrue(scanList.size() > 0);
        String scanId = scanList.get(0).getID();

        Results results = wrapper.results(UUID.fromString(scanId));
        Result result = results.getResults().stream().filter(res -> res.getType().equalsIgnoreCase(CxConstants.SAST)).findFirst().get();

        List<Predicate> predicates = wrapper.triageShow(UUID.fromString(scan.getProjectID()), result.getSimilarityId(), result.getType());

        Assert.assertNotNull(predicates);
    }

    @Test
    public void testTriageUpdate() throws Exception {
        List<Scan> scanList = wrapper.scanList(String.format("statuses=Completed"));
        Scan scan = scanList.get(0);
        Assert.assertTrue(scanList.size() > 0);
        String scanId = scanList.get(0).getID();

        Results results = wrapper.results(UUID.fromString(scanId));
        Result result = results.getResults().stream().filter(res -> res.getType().equalsIgnoreCase(CxConstants.SAST)).findFirst().get();

        try {
            wrapper.triageUpdate(UUID.fromString(scan.getProjectID()), result.getSimilarityId(), result.getType(), "confirmed", "Edited via Java Wrapper", "hih");
        } catch (Exception e) {
            fail("Triage update failed. Should not throw exception");
        }
    }
}
