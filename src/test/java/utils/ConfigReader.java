package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

/**
 * Reads config.json with environment-aware resolution, and resolves
 * "$ENV_VAR" style placeholders from system environment variables so
 * secrets are never hardcoded.
 *
 * Environment resolution priority (highest to lowest):
 *   1. -Denv=xxx            (CLI / IDE run configuration)
 *   2. ACTIVE_ENV            (OS environment variable, e.g. set by CI)
 *   3. config.json "environment" field
 *   4. "QA"                 (hardcoded fallback)
 */
public class ConfigReader {

    private static JsonNode root;

    static {
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream("config.json")) {
            if (in == null) {
                throw new RuntimeException("config.json not found on classpath");
            }
            root = new ObjectMapper().readTree(in);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.json", e);
        }
    }

    private ConfigReader() {
        // static-only utility class
    }

    public static String getActiveEnv() {
        String env = System.getProperty("env");
        if (env == null || env.isBlank()) env = System.getenv("ACTIVE_ENV");
        if (env == null || env.isBlank()) env = root.path("environment").asText(null);
        if (env == null || env.isBlank()) env = "QA";
        return env;
    }

    /** Looks in environments.<activeEnv>.<key> first, then falls back to root.<key>. */
    public static String get(String key) {
        return get(key, null);
    }

    public static String get(String key, String defaultValue) {
        JsonNode envNode = root.path("environments").path(getActiveEnv());
        JsonNode value = envNode.path(key);

        if (value.isMissingNode() || value.isNull()) {
            value = root.path(key);
        }
        if (value.isMissingNode() || value.isNull()) {
            return defaultValue;
        }
        String resolved = value.asText();
        return resolveIfEnvPlaceholder(resolved);
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key, null);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key, null);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    public static long getLong(String key, long defaultValue) {
        String value = get(key, null);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    /** If the config value starts with "$", resolve it from an environment variable (CI secrets). */
    private static String resolveIfEnvPlaceholder(String value) {
        if (value != null && value.startsWith("$")) {
            String envVarName = value.substring(1);
            String resolved = System.getenv(envVarName);
            return resolved != null ? resolved : value;
        }
        return value;
    }
}
