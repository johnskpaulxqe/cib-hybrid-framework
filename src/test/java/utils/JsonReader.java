package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Generic JSON reading utility — classpath resources (testdata/*.json) and
 * filesystem paths, dot-notation field lookups, and POJO (de)serialisation.
 */
public class JsonReader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonReader() {
        // static-only utility class
    }

    // ---------- Classpath ----------

    public static JsonNode readTree(String classpathPath) {
        try (InputStream in = JsonReader.class.getClassLoader().getResourceAsStream(classpathPath)) {
            if (in == null) throw new RuntimeException("Resource not found on classpath: " + classpathPath);
            return MAPPER.readTree(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON from classpath: " + classpathPath, e);
        }
    }

    public static <T> T readAs(String classpathPath, Class<T> clazz) {
        try (InputStream in = JsonReader.class.getClassLoader().getResourceAsStream(classpathPath)) {
            if (in == null) throw new RuntimeException("Resource not found on classpath: " + classpathPath);
            return MAPPER.readValue(in, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON from classpath: " + classpathPath, e);
        }
    }

    // ---------- Filesystem ----------

    public static JsonNode readTreeFromFile(String filePath) {
        try {
            return MAPPER.readTree(Files.readAllBytes(Path.of(filePath)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON from file: " + filePath, e);
        }
    }

    // ---------- Dot-notation field access ----------

    /** e.g. getField("testdata/users.json", "admin.username") */
    public static String getField(String classpathPath, String dotKey) {
        JsonNode node = getNode(classpathPath, dotKey);
        return (node == null || node.isMissingNode() || node.isNull()) ? null : node.asText();
    }

    public static int getFieldAsInt(String classpathPath, String dotKey, int defaultValue) {
        JsonNode node = getNode(classpathPath, dotKey);
        return (node == null || node.isMissingNode()) ? defaultValue : node.asInt(defaultValue);
    }

    public static boolean getFieldAsBoolean(String classpathPath, String dotKey, boolean defaultValue) {
        JsonNode node = getNode(classpathPath, dotKey);
        return (node == null || node.isMissingNode()) ? defaultValue : node.asBoolean(defaultValue);
    }

    public static JsonNode getNode(String classpathPath, String dotKey) {
        JsonNode current = readTree(classpathPath);
        for (String part : dotKey.split("\\.")) {
            current = current.path(part);
        }
        return current;
    }

    // ---------- String parsing (e.g. API response bodies) ----------

    public static JsonNode parseString(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON string", e);
        }
    }

    public static <T> T parseStringAs(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON string into " + clazz.getSimpleName(), e);
        }
    }

    // ---------- Serialisation ----------

    public static String toJson(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialise object to JSON", e);
        }
    }

    public static String toPrettyJson(Object object) {
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialise object to pretty JSON", e);
        }
    }
}
