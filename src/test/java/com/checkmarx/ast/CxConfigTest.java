package com.checkmarx.ast;

import com.checkmarx.ast.wrapper.CxConfig;
import com.checkmarx.ast.wrapper.CxConfig.InvalidCLIConfigException;
import com.checkmarx.ast.wrapper.CxConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

class CxConfigTest extends BaseTest {

    @Test
    void testApiKeyAuthentication() throws Exception {
        CxConfig config = CxConfig.builder()
                .apiKey("test-api-key")
                .build();
        
        Method toArgumentsMethod = CxConfig.class.getDeclaredMethod("toArguments");
        toArgumentsMethod.setAccessible(true);
        List<String> arguments = (List<String>) toArgumentsMethod.invoke(config);
        
        Assertions.assertTrue(arguments.contains(CxConstants.API_KEY));
        Assertions.assertTrue(arguments.contains("test-api-key"));
    }

    @Test
    void testClientIdSecretAuthentication() throws Exception {
        CxConfig config = CxConfig.builder()
                .clientId("test-client-id")
                .clientSecret("test-client-secret")
                .build();
        
        Method toArgumentsMethod = CxConfig.class.getDeclaredMethod("toArguments");
        toArgumentsMethod.setAccessible(true);
        List<String> arguments = (List<String>) toArgumentsMethod.invoke(config);
        
        Assertions.assertTrue(arguments.contains(CxConstants.CLIENT_ID));
        Assertions.assertTrue(arguments.contains("test-client-id"));
        Assertions.assertTrue(arguments.contains(CxConstants.CLIENT_SECRET));
        Assertions.assertTrue(arguments.contains("test-client-secret"));
    }

    @Test
    void testTenantAndBaseUris() throws Exception {
        CxConfig config = CxConfig.builder()
                .tenant("test-tenant")
                .baseUri("https://example.com")
                .baseAuthUri("https://auth.example.com")
                .build();
        
        Method toArgumentsMethod = CxConfig.class.getDeclaredMethod("toArguments");
        toArgumentsMethod.setAccessible(true);
        List<String> arguments = (List<String>) toArgumentsMethod.invoke(config);
        
        Assertions.assertTrue(arguments.contains(CxConstants.TENANT));
        Assertions.assertTrue(arguments.contains("test-tenant"));
        Assertions.assertTrue(arguments.contains(CxConstants.BASE_URI));
        Assertions.assertTrue(arguments.contains("https://example.com"));
        Assertions.assertTrue(arguments.contains(CxConstants.BASE_AUTH_URI));
        Assertions.assertTrue(arguments.contains("https://auth.example.com"));
    }

    @Test
    void testEmptyConfig() throws Exception {
        CxConfig config = CxConfig.builder().build();
        
        Method toArgumentsMethod = CxConfig.class.getDeclaredMethod("toArguments");
        toArgumentsMethod.setAccessible(true);
        List<String> arguments = (List<String>) toArgumentsMethod.invoke(config);
        
        Assertions.assertTrue(arguments.isEmpty());
    }

    @Test
    void testInvalidCLIConfigException() {
        Assertions.assertThrows(InvalidCLIConfigException.class, () -> {
            throw new InvalidCLIConfigException("Invalid configuration");
        });
    }
}
