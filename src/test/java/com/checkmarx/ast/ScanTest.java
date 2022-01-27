package com.checkmarx.ast;

import com.checkmarx.ast.scan.Scan;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScanTest extends BaseTest {

    @Test
    public void testScanShow() throws Exception {
        List<Scan> scanList = wrapper.scanList();
        Assertions.assertTrue(scanList.size() > 0);
        Scan scan = wrapper.scanShow(UUID.fromString(scanList.get(0).getID()));
        Assertions.assertEquals(scanList.get(0).getID(), scan.getID());
    }

    @Test
    public void testScanList() throws Exception {
        List<Scan> cxOutput = wrapper.scanList("limit=10");
        Assertions.assertTrue(cxOutput.size() <= 10);
    }

    @Test
    public void testScanCreate() throws Exception {
        Map<String, String> params = commonParams();
        Scan scan = wrapper.scanCreate(params);
        Assertions.assertEquals("Completed", wrapper.scanShow(UUID.fromString(scan.getID())).getStatus());
    }

}
