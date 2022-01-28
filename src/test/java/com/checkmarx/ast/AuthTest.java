package com.checkmarx.ast;

import com.checkmarx.ast.wrapper.CxConfig;
import com.checkmarx.ast.wrapper.CxException;
import com.checkmarx.ast.wrapper.CxWrapper;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class AuthTest extends BaseTest {

    @Test
    void testAuthValidate() throws CxException, IOException, InterruptedException {
        Assertions.assertNotNull(wrapper.authValidate());
    }

    @Test
    void testAuthFailure() {
        CxConfig cxConfig = getConfig();
        cxConfig.setBaseAuthUri("wrongAuth");
        Assertions.assertThrows(CxException.class, () -> new CxWrapper(cxConfig, getLogger()).authValidate());
    }
}
