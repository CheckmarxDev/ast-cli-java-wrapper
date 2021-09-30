package com.checkmarx.ast;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class AuthTest extends BaseTest {

    @Test
    public void testAuthValidate() throws IOException, InterruptedException {
        CLIOutput<String> output = wrapper.authValidate();
        Assert.assertEquals(0, output.getExitCode());
    }

    @Test
    public void testAuthFailure()
            throws IOException, InterruptedException, CLIConfig.InvalidCLIConfigException, URISyntaxException {
        CLIConfig config = getConfig();
        config.setBaseAuthUri("wrongAuth");
        CLIOutput<String> output = new CLIWrapper(config, getLogger()).authValidate();
        Assert.assertEquals(1, output.getExitCode());
    }
}
