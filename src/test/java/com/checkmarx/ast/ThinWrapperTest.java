package com.checkmarx.ast;

import com.checkmarx.ast.scan.Scan;
import com.checkmarx.ast.wrapper.CxException;
import com.checkmarx.ast.wrapper.CxThinWrapper;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ThinWrapperTest extends BaseTest {

    @SneakyThrows
    @Test
    public void testThinWrapper() {
        CxThinWrapper wrapper = new CxThinWrapper(getLogger());
        String result = wrapper.run("scan list --format json --filter limit=10");
        List<Scan> scanList = Scan.listFromLine(result);
        Assert.assertTrue(scanList.size() <= 10);
    }

    @SneakyThrows
    @Test
    public void testThinWrapperFail() {
        CxThinWrapper wrapper = new CxThinWrapper(getLogger());
        String arguments = "scan create -s . --project-name thin-wrapper-test --sast-preset invalid-preset";
        CxException e = Assert.assertThrows(CxException.class, () -> wrapper.run(arguments));
        Assert.assertTrue(e.getMessage().contains("--sast-preset"));
    }
}
