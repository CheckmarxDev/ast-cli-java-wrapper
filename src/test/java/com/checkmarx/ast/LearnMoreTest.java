package com.checkmarx.ast;

import com.checkmarx.ast.kicsRealtimeResults.kicsRealtimeResults;
import com.checkmarx.ast.learnmore.LearnMoreDescriptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class LearnMoreTest extends BaseTest {

    @Test
    void testLearnMoreDescriptionsSuccessfulList() throws Exception {
        List<LearnMoreDescriptions> learnMoreDetails = wrapper.learnMore("10308959669028119927");
        Assertions.assertTrue(learnMoreDetails.size() > 0);
    }

    @Test
    void testLearnMoreDescriptionsFailureNullQueryId() throws Exception {
        List<LearnMoreDescriptions> learnMoreDetails = wrapper.learnMore("");
        Assertions.assertNull(learnMoreDetails);
    }
}
