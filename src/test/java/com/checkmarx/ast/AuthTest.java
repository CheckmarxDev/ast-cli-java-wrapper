package com.checkmarx.ast;

import com.checkmarx.ast.wrapper.CxConfig;
import com.checkmarx.ast.wrapper.CxException;
import com.checkmarx.ast.wrapper.CxWrapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class AuthTest extends BaseTest {

    @Test
    public void testAuthValidate() throws CxException, IOException, InterruptedException {
        Assert.assertNotNull(wrapper.authValidate());
    }

    @Test
    public void testAuthFailure() {
        CxConfig cxConfig = getConfig();
        cxConfig.setBaseAuthUri("wrongAuth");
        Assert.assertThrows(CxException.class, () -> new CxWrapper(cxConfig, getLogger()).authValidate());
    }
}
