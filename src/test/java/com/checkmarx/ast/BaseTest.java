package com.checkmarx.ast;

import com.checkmarx.ast.wrapper.CLIConfig;
import com.checkmarx.ast.wrapper.CLIWrapper;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTest {

    protected CLIWrapper wrapper;

    @Before
    public void init() throws Exception {
        wrapper = new CLIWrapper(getConfig(), getLogger());
    }

    private static final String CX_BASE_URI = getEnvOrNull("CX_BASE_URI");
    private static final String CX_BASE_AUTH_URI = getEnvOrNull("CX_BASE_AUTH_URI");
    private static final String CX_TENANT = getEnvOrNull("CX_TENANT");
    private static final String CX_APIKEY = getEnvOrNull("CX_APIKEY");
    private static final String CX_CLIENT_ID = getEnvOrNull("CX_CLIENT_ID");
    private static final String CX_CLIENT_SECRET = getEnvOrNull("CX_CLIENT_SECRET");
    private static final String CX_ADDITIONAL_PARAMETERS = getEnvOrNull("CX_ADDITIONAL_PARAMETERS");
    private static final String PATH_TO_EXECUTABLE = getEnvOrNull("PATH_TO_EXECUTABLE");
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected Logger getLogger() {
        return logger;
    }

    protected static CLIConfig getConfig() {
        return CLIConfig.builder()
                        .baseUri(CX_BASE_URI)
                        .baseAuthUri(CX_BASE_AUTH_URI)
                        .tenant(CX_TENANT)
                        .apiKey(CX_APIKEY)
                        .clientId(CX_CLIENT_ID)
                        .clientSecret(CX_CLIENT_SECRET)
                        .additionalParameters(CX_ADDITIONAL_PARAMETERS)
                        .pathToExecutable(PATH_TO_EXECUTABLE)
                        .build();
    }

    private static String getEnvOrNull(String key) {
        return System.getenv().getOrDefault(key, null);
    }
}
