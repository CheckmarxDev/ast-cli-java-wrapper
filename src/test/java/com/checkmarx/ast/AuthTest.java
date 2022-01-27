package com.checkmarx.ast;

import com.checkmarx.ast.wrapper.CxConfig;
import com.checkmarx.ast.wrapper.CxException;
import com.checkmarx.ast.wrapper.CxWrapper;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class AuthTest extends BaseTest {

    @Test
    public void testAuthValidate() throws CxException, IOException, InterruptedException {
        Assertions.assertNotNull(wrapper.authValidate());
    }

    @Test
    public void testAuthFailure() {
        CxConfig cxConfig = getConfig();
        cxConfig.setBaseAuthUri("wrongAuth");
        Assertions.assertThrows(CxException.class, () -> new CxWrapper(cxConfig, getLogger()).authValidate());
    }
}
