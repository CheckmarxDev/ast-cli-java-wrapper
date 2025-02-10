package com.checkmarx.ast;

import com.checkmarx.ast.predicate.Predicate;
import com.checkmarx.ast.results.Results;
import com.checkmarx.ast.results.result.Result;
import com.checkmarx.ast.scan.Scan;
import com.checkmarx.ast.wrapper.CxConstants;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

class PredicateTest extends BaseTest {

    public static final String TO_VERIFY = "TO_VERIFY";
    public static final String HIGH = "HIGH";

    @Test
    void testTriage() throws Exception {
        Map<String, String> params = commonParams();
        Scan scan = wrapper.scanCreate(params);
        UUID scanId = UUID.fromString(scan.getId());

        Assertions.assertEquals("Completed", wrapper.scanShow(scanId).getStatus());

        Results results = wrapper.results(scanId);
        Result result = results.getResults().stream().filter(res -> res.getType().equalsIgnoreCase(CxConstants.SAST)).findFirst().get();

        List<Predicate> predicates = wrapper.triageShow(UUID.fromString(scan.getProjectId()), result.getSimilarityId(), result.getType());

        Assertions.assertNotNull(predicates);

        try {
            wrapper.triageUpdate(UUID.fromString(scan.getProjectId()), result.getSimilarityId(), result.getType(), TO_VERIFY, "Edited via Java Wrapper", HIGH);
        } catch (Exception e) {
            Assertions.fail("Triage update failed. Should not throw exception");
        }

        try {
            wrapper.triageUpdate(UUID.fromString(scan.getProjectId()), result.getSimilarityId(), result.getType(), result.getState(), "Edited back to normal", result.getSeverity());
        } catch (Exception e) {
            Assertions.fail("Triage update failed. Should not throw exception");
        }
    }

    @Test
    @Ignore("Ignore this tests until get states api will be in production")
    void testGetStates() throws Exception {
        List<Predicate> states = wrapper.triageGetStates(false);
        Assertions.assertNotNull(states);
    }
}
