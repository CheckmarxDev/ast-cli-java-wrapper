package com.checkmarx.ast;

import com.checkmarx.ast.wrapper.CxConfig;
import com.checkmarx.ast.wrapper.CxOutput;
import com.checkmarx.ast.wrapper.CxWrapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class AuthTest extends BaseTest {

    @Test
    public void testAuthValidate() throws IOException, InterruptedException {
        CxOutput<String> cxOutput = wrapper.authValidate();
        Assert.assertEquals(0, cxOutput.getExitCode());
    }

    @Test
    public void testAuthFailure()
            throws IOException, InterruptedException, CxConfig.InvalidCLIConfigException, URISyntaxException {
        CxConfig cxConfig = getConfig();
        cxConfig.setBaseAuthUri("wrongAuth");
        CxOutput<String> cxOutput = new CxWrapper(cxConfig, getLogger()).authValidate();
        Assert.assertEquals(1, cxOutput.getExitCode());
    }
}
