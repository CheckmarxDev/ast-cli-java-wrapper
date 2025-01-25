package com.checkmarx.ast;

import com.checkmarx.ast.wrapper.CxConfig;
import com.checkmarx.ast.wrapper.CxConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class CxConfigTest extends BaseTest {

    @Test
    void testConfig_WhenUsingApiKey_GeneratesCorrectArguments() {
        // Arrange
        CxConfig config = CxConfig.builder()
                .apiKey("test-api-key")
                .tenant("test-tenant")
                .baseUri("https://test.checkmarx.com")
                .build();

        // Act
        List<String> arguments = config.toArguments();

        // Assert
        Assertions.assertTrue(arguments.contains(CxConstants.API_KEY), "Should contain API_KEY argument");
        Assertions.assertTrue(arguments.contains("test-api-key"), "Should contain API key value");
        Assertions.assertTrue(arguments.contains(CxConstants.TENANT), "Should contain TENANT argument");
        Assertions.assertTrue(arguments.contains("test-tenant"), "Should contain tenant value");
        Assertions.assertTrue(arguments.contains(CxConstants.BASE_URI), "Should contain BASE_URI argument");
        Assertions.assertTrue(arguments.contains("https://test.checkmarx.com"), "Should contain base URI value");
    }

    @Test
    void testConfig_WhenUsingClientCredentials_GeneratesCorrectArguments() {
        // Arrange
        CxConfig config = CxConfig.builder()
                .clientId("test-client")
                .clientSecret("test-secret")
                .tenant("test-tenant")
                .build();

        // Act
        List<String> arguments = config.toArguments();

        // Assert
        Assertions.assertTrue(arguments.contains(CxConstants.CLIENT_ID), "Should contain CLIENT_ID argument");
        Assertions.assertTrue(arguments.contains("test-client"), "Should contain client ID value");
        Assertions.assertTrue(arguments.contains(CxConstants.CLIENT_SECRET), "Should contain CLIENT_SECRET argument");
        Assertions.assertTrue(arguments.contains("test-secret"), "Should contain client secret value");
        Assertions.assertTrue(arguments.contains(CxConstants.TENANT), "Should contain TENANT argument");
        Assertions.assertTrue(arguments.contains("test-tenant"), "Should contain tenant value");
    }

    @Test
    void testConfig_WhenSettingAdditionalParameters_ParsesCorrectly() {
        // Arrange
        CxConfig config = CxConfig.builder().build();
        String params = "--param1 value1 --param2 \"value 2\"";

        // Act
        config.setAdditionalParameters(params);
        List<String> additionalParams = config.getAdditionalParameters();

        // Assert
        List<String> expected = Arrays.asList("--param1", "value1", "--param2", "value 2");
        Assertions.assertEquals(expected, additionalParams, "Additional parameters should be parsed correctly");
    }

    @Test
    void testConfig_WhenNoAuthProvided_GeneratesMinimalArguments() {
        // Arrange
        CxConfig config = CxConfig.builder()
                .tenant("test-tenant")
                .build();

        // Act
        List<String> arguments = config.toArguments();

        // Assert
        Assertions.assertTrue(arguments.contains(CxConstants.TENANT), "Should contain TENANT argument");
        Assertions.assertTrue(arguments.contains("test-tenant"), "Should contain tenant value");
        Assertions.assertFalse(arguments.contains(CxConstants.API_KEY), "Should not contain API_KEY argument");
        Assertions.assertFalse(arguments.contains(CxConstants.CLIENT_ID), "Should not contain CLIENT_ID argument");
    }
}
