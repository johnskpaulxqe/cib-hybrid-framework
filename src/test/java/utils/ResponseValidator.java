package utils;

import io.restassured.response.Response;
import org.testng.Assert;

import java.util.List;

/**
 * Assertions and extraction helpers for REST Assured Response objects.
 * Pairs with ApiUtils / RequestBuilder to close out the API layer.
 */
public class ResponseValidator {

    private static final long DEFAULT_SLA_MS = ConfigReader.getLong("apiSlaMs", 3000L);

    private ResponseValidator() {
        // static-only utility class
    }

    // ---------- Status ----------

    public static void assertStatusCode(Response response, int expected) {
        Assert.assertEquals(response.getStatusCode(), expected,
                "Unexpected status code. Body: " + response.getBody().asString());
    }

    public static void assertStatusCodeIn(Response response, int... expectedCodes) {
        int actual = response.getStatusCode();
        for (int code : expectedCodes) {
            if (actual == code) return;
        }
        Assert.fail("Status code " + actual + " not in expected set " + java.util.Arrays.toString(expectedCodes));
    }

    public static void assertSuccess(Response response) {
        int code = response.getStatusCode();
        Assert.assertTrue(code >= 200 && code < 300, "Expected 2xx, got " + code);
    }

    // ---------- JSON fields ----------

    public static void assertFieldEquals(Response response, String jsonPath, Object expected) {
        Object actual = response.jsonPath().get(jsonPath);
        Assert.assertEquals(actual, expected, "Field mismatch at [" + jsonPath + "]");
    }

    public static void assertFieldNotNull(Response response, String jsonPath) {
        Object actual = response.jsonPath().get(jsonPath);
        Assert.assertNotNull(actual, "Expected field [" + jsonPath + "] to be present and non-null");
    }

    public static void assertFieldContains(Response response, String jsonPath, String expectedSubstring) {
        String actual = response.jsonPath().getString(jsonPath);
        Assert.assertTrue(actual != null && actual.contains(expectedSubstring),
                "Field [" + jsonPath + "] value [" + actual + "] did not contain [" + expectedSubstring + "]");
    }

    // ---------- Arrays ----------

    public static void assertArrayNotEmpty(Response response, String jsonPath) {
        List<?> list = response.jsonPath().getList(jsonPath);
        Assert.assertTrue(list != null && !list.isEmpty(), "Expected non-empty array at [" + jsonPath + "]");
    }

    public static void assertArraySize(Response response, String jsonPath, int expectedSize) {
        List<?> list = response.jsonPath().getList(jsonPath);
        Assert.assertEquals(list == null ? 0 : list.size(), expectedSize, "Array size mismatch at [" + jsonPath + "]");
    }

    // ---------- Body ----------

    public static void assertBodyContains(Response response, String expectedSubstring) {
        Assert.assertTrue(response.getBody().asString().contains(expectedSubstring),
                "Response body did not contain [" + expectedSubstring + "]");
    }

    // ---------- Headers ----------

    public static void assertHeaderEquals(Response response, String headerName, String expectedValue) {
        Assert.assertEquals(response.getHeader(headerName), expectedValue, "Header mismatch: " + headerName);
    }

    public static void assertContentTypeJson(Response response) {
        String contentType = response.getContentType();
        Assert.assertTrue(contentType != null && contentType.contains("application/json"),
                "Expected JSON content type, got: " + contentType);
    }

    // ---------- Performance ----------

    public static void assertResponseTimeBelow(Response response, long maxMillis) {
        long actual = response.getTime();
        Assert.assertTrue(actual < maxMillis,
                "Response time " + actual + "ms exceeded max " + maxMillis + "ms");
    }

    public static void assertResponseTimeWithinSla(Response response) {
        assertResponseTimeBelow(response, DEFAULT_SLA_MS);
    }

    // ---------- Extraction ----------

    public static String extractString(Response response, String jsonPath) {
        return response.jsonPath().getString(jsonPath);
    }

    public static int extractInt(Response response, String jsonPath) {
        return response.jsonPath().getInt(jsonPath);
    }

    public static List<?> extractList(Response response, String jsonPath) {
        return response.jsonPath().getList(jsonPath);
    }

    public static String extractResponseBody(Response response) {
        return response.getBody().asString();
    }

    public static int getStatusCode(Response response) {
        return response.getStatusCode();
    }
}
