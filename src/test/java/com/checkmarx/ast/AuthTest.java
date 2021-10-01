package com.checkmarx.ast;

import com.checkmarx.ast.wrapper.CLIConfig;
import com.checkmarx.ast.wrapper.CLIOutput;
import com.checkmarx.ast.wrapper.CLIWrapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class AuthTest extends BaseTest {

    @Test
    public void testAuthValidate() throws IOException, InterruptedException {
        CLIOutput<String> cliOutput = wrapper.authValidate();
        Assert.assertEquals(0, cliOutput.getExitCode());
    }

    @Test
    public void testAuthFailure()
            throws IOException, InterruptedException, CLIConfig.InvalidCLIConfigException, URISyntaxException {
        CLIConfig cliConfig = getConfig();
        cliConfig.setBaseAuthUri("wrongAuth");
        CLIOutput<String> cliOutput = new CLIWrapper(cliConfig, getLogger()).authValidate();
        Assert.assertEquals(1, cliOutput.getExitCode());
    }
}
