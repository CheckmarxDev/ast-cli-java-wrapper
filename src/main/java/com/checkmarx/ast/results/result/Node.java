package com.checkmarx.ast.results.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Value
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Node {

    String id;
    int line;
    String name;
    int column;
    int length;
    String method;
    int nodeId;
    String domType;
    String fileName;
    String fullName;
    String typeName;
    String methodLine;
    String definitions;

    public Node(@JsonProperty("id") String id,
                @JsonProperty("line") int line,
                @JsonProperty("name") String name,
                @JsonProperty("column") int column,
                @JsonProperty("length") int length,
                @JsonProperty("method") String method,
                @JsonProperty("nodeID") int nodeId,
                @JsonProperty("domType") String domType,
                @JsonProperty("fileName") String fileName,
                @JsonProperty("fullName") String fullName,
                @JsonProperty("typeName") String typeName,
                @JsonProperty("methodLine") String methodLine,
                @JsonProperty("definitions") String definitions) {
        this.id = id;
        this.line = line;
        this.name = name;
        this.column = column;
        this.length = length;
        this.method = method;
        this.nodeId = nodeId;
        this.domType = domType;
        this.fileName = fileName;
        this.fullName = fullName;
        this.typeName = typeName;
        this.methodLine = methodLine;
        this.definitions = definitions;
    }

    public static <T> T fromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructType(Node.class));
    }

    public static <T> List<T> listFromLine(String line) {
        return parse(line, TypeFactory.defaultInstance().constructCollectionType(List.class, Node.class));
    }

    private static <T> T parse(String line, JavaType type) {
        T result = null;
        try {
            if (!StringUtils.isBlank(line) && isValidJSON(line)) {
                result = new ObjectMapper().readValue(line, type);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static boolean isValidJSON(final String json) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Node node = (Node) obj;
        return line == node.line &&
                column == node.column &&
                length == node.length &&
                Objects.equals(name, node.name) &&
                Objects.equals(method, node.method) &&
                Objects.equals(domType, node.domType) &&
                Objects.equals(fileName, node.fileName) &&
                Objects.equals(fullName, node.fullName) &&
                Objects.equals(methodLine, node.methodLine);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
