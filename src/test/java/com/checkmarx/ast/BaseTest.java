package com.checkmarx.ast;

import com.checkmarx.ast.wrapper.CxConfig;
import com.checkmarx.ast.wrapper.CxConstants;
import com.checkmarx.ast.wrapper.CxWrapper;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseTest {

    protected CxWrapper wrapper;

    @Before
    public void init() throws Exception {
        wrapper = new CxWrapper(getConfig(), getLogger());
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

    protected static CxConfig getConfig() {
        return CxConfig.builder()
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

    protected Map<String, String> commonParams() {
        Map<String, String> params = new HashMap<>();
        params.put(CxConstants.PROJECT_NAME, "JavaWrapperTestCases");
        params.put(CxConstants.SOURCE, ".");
        params.put(CxConstants.FILE_FILTER, "*.java");
        params.put(CxConstants.SAST_PRESET_NAME, "Checkmarx Default");
        return params;
    }
}
