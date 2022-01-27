package com.checkmarx.ast;

import com.checkmarx.ast.scan.Scan;
import com.checkmarx.ast.wrapper.CxConstants;
import com.checkmarx.ast.wrapper.CxException;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScanTest extends BaseTest {

    @Test
    public void testScanShow() throws Exception {
        List<Scan> scanList = wrapper.scanList();
        Assert.assertTrue(scanList.size() > 0);
        Scan scan = wrapper.scanShow(UUID.fromString(scanList.get(0).getID()));
        Assert.assertEquals(scanList.get(0).getID(), scan.getID());
    }

    @Test
    public void testScanList() throws Exception {
        List<Scan> cxOutput = wrapper.scanList("limit=10");
        Assert.assertTrue(cxOutput.size() <= 10);
    }

    @Test
    public void testScanCreate() throws Exception {
        Map<String, String> params = commonParams();
        Scan scan = wrapper.scanCreate(params);
        Assert.assertEquals("Completed",
                            wrapper.scanShow(UUID.fromString(scan.getID())).getStatus());
    }

    @Test
    public void testScanCreateWrongPreset() {
        Map<String, String> params = commonParams();
        params.put(CxConstants.SAST_PRESET_NAME, "InvalidPreset");
        Assert.assertThrows(CxException.class, () -> wrapper.scanCreate(params));
    }
}
