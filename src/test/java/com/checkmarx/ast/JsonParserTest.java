package com.checkmarx.ast;

import com.checkmarx.ast.utils.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

class JsonParserTest extends BaseTest {

    @Test
    void testValidJsonParsing_WhenValidJsonProvided_ReturnsParsedObject() {
        // Arrange
        String validJson = "{\"name\": \"test\", \"value\": 123}";
        TypeFactory typeFactory = TypeFactory.defaultInstance();
        JavaType type = typeFactory.constructMapType(Map.class, String.class, Object.class);
        
        // Act
        Map<String, Object> result = JsonParser.parse(validJson, type);
        
        // Assert
        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertEquals("test", result.get("name"), "Name value should match");
        Assertions.assertEquals(123, result.get("value"), "Numeric value should match");
    }

    @Test
    void testNullAndEmptyInput_WhenInvalidInputProvided_ReturnsNull() {
        // Arrange
        TypeFactory typeFactory = TypeFactory.defaultInstance();
        JavaType type = typeFactory.constructMapType(Map.class, String.class, String.class);
        
        // Act & Assert
        Assertions.assertNull(JsonParser.parse(null, type), "Null input should return null");
        Assertions.assertNull(JsonParser.parse("", type), "Empty string should return null");
        Assertions.assertNull(JsonParser.parse("   ", type), "Whitespace should return null");
    }

    @Test
    void testInvalidJson_WhenMalformedJsonProvided_ReturnsNull() {
        // Arrange
        TypeFactory typeFactory = TypeFactory.defaultInstance();
        JavaType type = typeFactory.constructMapType(Map.class, String.class, String.class);
        
        // Act & Assert
        Assertions.assertNull(JsonParser.parse("{invalid:json}", type), "Invalid JSON should return null");
        Assertions.assertNull(JsonParser.parse("{\"key\":\"value\"", type), "Incomplete JSON should return null");
    }

    @Test
    void testListParsing_WhenValidJsonArrayProvided_ReturnsParsedList() {
        // Arrange
        String jsonList = "[\"item1\", \"item2\", \"item3\"]";
        TypeFactory typeFactory = TypeFactory.defaultInstance();
        JavaType type = typeFactory.constructCollectionType(List.class, String.class);
        
        // Act
        List<String> result = JsonParser.parse(jsonList, type);
        
        // Assert
        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertEquals(Arrays.asList("item1", "item2", "item3"), result, "List contents should match");
    }
}
