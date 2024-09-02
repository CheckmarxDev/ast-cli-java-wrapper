package com.checkmarx.ast;

import com.checkmarx.ast.wrapper.CxConfig;
import com.checkmarx.ast.wrapper.CxConstants;
import com.checkmarx.ast.wrapper.CxWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseTest {

    private static final String CX_BASE_URI = getEnvOrNull("CX_BASE_URI");
    private static final String CX_BASE_AUTH_URI = getEnvOrNull("CX_BASE_AUTH_URI");
    private static final String CX_TENANT = getEnvOrNull("CX_TENANT");
    private static final String CX_APIKEY = "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI0N2Y1NDZlNS02YjFlLTQ0NjgtOGM0Yi0zNjdmNDcwNzMxZTYifQ.eyJpYXQiOjE3MDA0NzU0NzksImp0aSI6IjhkMjE5MmUyLWYwZmMtNDJlMy1hN2JiLTJhMWQ2MDcxNmUxNiIsImlzcyI6Imh0dHBzOi8vZGV1LmlhbS5jaGVja21hcngubmV0L2F1dGgvcmVhbG1zL2N4X2FzdF9yZF9nYWxhdGljYV9jYW5hcnkiLCJhdWQiOiJodHRwczovL2RldS5pYW0uY2hlY2ttYXJ4Lm5ldC9hdXRoL3JlYWxtcy9jeF9hc3RfcmRfZ2FsYXRpY2FfY2FuYXJ5Iiwic3ViIjoiOWRiZWVjZTAtNTZlNy00NDlhLTk2M2YtZjQ5Yzk5Yzk1Y2NjIiwidHlwIjoiT2ZmbGluZSIsImF6cCI6ImFzdC1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiYmYxOGZmNDQtZTIyNC00ZTI0LTg0ZmMtZWIyMzRlMjVlYmRmIiwic2NvcGUiOiIgb2ZmbGluZV9hY2Nlc3MiLCJzaWQiOiJiZjE4ZmY0NC1lMjI0LTRlMjQtODRmYy1lYjIzNGUyNWViZGYifQ.LR9UX6PFXn5KH56IQUdcYukvnZTKUTQcYBKlWICJwpg";
    private static final String CX_CLIENT_ID = getEnvOrNull("CX_CLIENT_ID");
    private static final String CX_CLIENT_SECRET = getEnvOrNull("CX_CLIENT_SECRET");
    private static final String CX_ADDITIONAL_PARAMETERS = "--debug";
    private static final String PATH_TO_EXECUTABLE = getEnvOrNull("PATH_TO_EXECUTABLE");
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected CxWrapper wrapper;
    private String projectId;

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

    @BeforeEach
    public void init() throws Exception {
        wrapper = new CxWrapper(getConfig(), getLogger());
    }

    protected Logger getLogger() {
        return logger;
    }

    protected Map<String, String> commonParams() {
        Map<String, String> params = new HashMap<>();
        params.put(CxConstants.PROJECT_NAME, "cli-java-wrapper-tests");
        params.put(CxConstants.SOURCE, "./src/");
        params.put(CxConstants.FILE_FILTER, "!test");
        params.put(CxConstants.BRANCH, "main");
        params.put(CxConstants.SAST_PRESET_NAME, "Checkmarx Default");
        params.put(CxConstants.AGENT, "CLI-Java-Wrapper");
        return params;
    }
}
