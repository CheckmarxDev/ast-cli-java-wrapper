package com.checkmarx.ast;

import com.checkmarx.ast.predicate.Predicate;
import com.checkmarx.ast.results.Results;
import com.checkmarx.ast.results.result.Result;
import com.checkmarx.ast.scan.Scan;
import com.checkmarx.ast.wrapper.CxConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

class PredicateTest extends BaseTest {

    @Test
    void testTriageShow() throws Exception {
        Scan scan = wrapper.scanShow(UUID.fromString(CX_SCAN_ID));

        Results results = wrapper.results(UUID.fromString(CX_SCAN_ID));
        Result result = results.getResults().stream().filter(res -> res.getType().equalsIgnoreCase(CxConstants.SAST)).findFirst().get();

        List<Predicate> predicates = wrapper.triageShow(UUID.fromString(scan.getProjectId()), result.getSimilarityId(), result.getType());

        Assertions.assertNotNull(predicates);
    }

    @Test
    void testTriageUpdate() throws Exception {
        Scan scan = wrapper.scanShow(UUID.fromString(CX_SCAN_ID));

        Results results = wrapper.results(UUID.fromString(CX_SCAN_ID));
        Result result = results.getResults().stream().filter(res -> res.getType().equalsIgnoreCase(CxConstants.SAST)).findFirst().get();

        try {
            wrapper.triageUpdate(UUID.fromString(scan.getProjectId()), result.getSimilarityId(), result.getType(), "to_verify", "Edited via Java Wrapper", "high");
        } catch (Exception e) {
            Assertions.fail("Triage update failed. Should not throw exception");
        }
    }
}
