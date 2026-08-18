package utils;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Typed access to the three test data files: users.json, apidata.json, dbdata.json.
 * Step definitions call through here rather than reading raw JSON directly.
 */
public class TestDataLoader {

    private static final String USERS_FILE = "testdata/users.json";
    private static final String API_FILE = "testdata/apidata.json";
    private static final String DB_FILE = "testdata/dbdata.json";

    private TestDataLoader() {
        // static-only utility class
    }

    // ---------- Users ----------

    public static String getUsername(String role) {
        return JsonReader.getField(USERS_FILE, role + ".username");
    }

    public static String getPassword(String role) {
        return JsonReader.getField(USERS_FILE, role + ".password");
    }

    public static String getUserEmail(String role) {
        return JsonReader.getField(USERS_FILE, role + ".email");
    }

    public static String getEntityId(String role) {
        return JsonReader.getField(USERS_FILE, role + ".entityId");
    }

    // ---------- API ----------

    public static String getApiEndpoint(String key) {
        return JsonReader.getField(API_FILE, "endpoints." + key);
    }

    public static JsonNode getRequestBody(String key) {
        return JsonReader.getNode(API_FILE, "requestBodies." + key);
    }

    public static String getApiHeader(String key) {
        return JsonReader.getField(API_FILE, "headers." + key);
    }

    public static String getApiField(String dotKey) {
        return JsonReader.getField(API_FILE, dotKey);
    }

    // ---------- DB ----------

    public static String getDbQuery(String key) {
        return JsonReader.getField(DB_FILE, "queries." + key);
    }

    public static String getDbExpectedValue(String dotKey) {
        return JsonReader.getField(DB_FILE, "expectedValues." + dotKey);
    }

    public static String getDbTableName(String key) {
        return JsonReader.getField(DB_FILE, "tables." + key);
    }
}
