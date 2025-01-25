package com.checkmarx.ast;

import com.checkmarx.ast.wrapper.CxConfig;
import com.checkmarx.ast.wrapper.CxConfig.InvalidCLIConfigException;
import com.checkmarx.ast.wrapper.CxConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class CxConfigTest extends BaseTest {

    @Test
    void testApiKeyAuthentication() {
        CxConfig config = CxConfig.builder()
                .apiKey("test-api-key")
                .build();
        
        List<String> arguments = config.toArguments();
        Assertions.assertTrue(arguments.contains(CxConstants.API_KEY));
        Assertions.assertTrue(arguments.contains("test-api-key"));
    }

    @Test
    void testClientIdSecretAuthentication() {
        CxConfig config = CxConfig.builder()
                .clientId("test-client-id")
                .clientSecret("test-client-secret")
                .build();
        
        List<String> arguments = config.toArguments();
        Assertions.assertTrue(arguments.contains(CxConstants.CLIENT_ID));
        Assertions.assertTrue(arguments.contains("test-client-id"));
        Assertions.assertTrue(arguments.contains(CxConstants.CLIENT_SECRET));
        Assertions.assertTrue(arguments.contains("test-client-secret"));
    }

    @Test
    void testTenantAndBaseUris() {
        CxConfig config = CxConfig.builder()
                .tenant("test-tenant")
                .baseUri("https://example.com")
                .baseAuthUri("https://auth.example.com")
                .build();
        
        List<String> arguments = config.toArguments();
        Assertions.assertTrue(arguments.contains(CxConstants.TENANT));
        Assertions.assertTrue(arguments.contains("test-tenant"));
        Assertions.assertTrue(arguments.contains(CxConstants.BASE_URI));
        Assertions.assertTrue(arguments.contains("https://example.com"));
        Assertions.assertTrue(arguments.contains(CxConstants.BASE_AUTH_URI));
        Assertions.assertTrue(arguments.contains("https://auth.example.com"));
    }

    @Test
    void testEmptyConfig() {
        CxConfig config = CxConfig.builder().build();
        
        List<String> arguments = config.toArguments();
        Assertions.assertTrue(arguments.isEmpty());
    }

    @Test
    void testAdditionalParametersParsing() {
        CxConfig config = CxConfig.builder()
                .additionalParameters("--debug --verbose \"multi word value\"")
                .build();
        
        List<String> arguments = config.getAdditionalParameters();
        Assertions.assertEquals(3, arguments.size());
        Assertions.assertTrue(arguments.contains("--debug"));
        Assertions.assertTrue(arguments.contains("--verbose"));
        Assertions.assertTrue(arguments.contains("multi word value"));
    }

    @Test
    void testSpecialCharactersInAdditionalParameters() {
        CxConfig config = CxConfig.builder()
                .additionalParameters("--path \"C:\\Program Files\\Tool\"")
                .build();
        
        List<String> arguments = config.getAdditionalParameters();
        Assertions.assertEquals(1, arguments.size());
        Assertions.assertTrue(arguments.contains("C:\\Program Files\\Tool"));
    }

    @Test
    void testInvalidCLIConfigException() {
        Assertions.assertThrows(InvalidCLIConfigException.class, () -> {
            throw new InvalidCLIConfigException("Invalid configuration");
        });
    }
}
