package utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

/**
 * Base REST Assured configuration and quick one-line call helpers.
 * For anything needing path params, multiple custom headers, or form data,
 * use RequestBuilder instead.
 */
public class ApiUtils {

    private static final int CONNECTION_TIMEOUT_MS = ConfigReader.getInt("apiConnectionTimeoutMs", 10000);

    private ApiUtils() {
        // static-only utility class
    }

    /** Call once per @api scenario (from Hooks.java @Before) to point REST Assured at the active environment. */
    public static void setup() {
        RestAssured.baseURI = ConfigReader.get("apiBaseUrl");
        LogUtil.info(ApiUtils.class, "API base URI set to {}", RestAssured.baseURI);
    }

    public static RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .contentType("application/json")
                .accept("application/json");
    }

    // ---------- GET ----------

    public static Response get(String endpoint) {
        return getRequestSpec().get(endpoint);
    }

    public static Response get(String endpoint, Map<String, ?> queryParams) {
        return getRequestSpec().queryParams(queryParams).get(endpoint);
    }

    public static Response getWithAuth(String endpoint, String token) {
        return getRequestSpec().header("Authorization", "Bearer " + token).get(endpoint);
    }

    public static Response getWithAuth(String endpoint, Map<String, ?> queryParams, String token) {
        return getRequestSpec().header("Authorization", "Bearer " + token).queryParams(queryParams).get(endpoint);
    }

    public static Response getWithHeaders(String endpoint, Map<String, String> headers) {
        return getRequestSpec().headers(headers).get(endpoint);
    }

    // ---------- POST ----------

    public static Response post(String endpoint, Object body) {
        return getRequestSpec().body(body).post(endpoint);
    }

    public static Response postWithAuth(String endpoint, Object body, String token) {
        return getRequestSpec().header("Authorization", "Bearer " + token).body(body).post(endpoint);
    }

    // ---------- PUT ----------

    public static Response put(String endpoint, Object body) {
        return getRequestSpec().body(body).put(endpoint);
    }

    public static Response putWithAuth(String endpoint, Object body, String token) {
        return getRequestSpec().header("Authorization", "Bearer " + token).body(body).put(endpoint);
    }

    // ---------- PATCH ----------

    public static Response patch(String endpoint, Object body) {
        return getRequestSpec().body(body).patch(endpoint);
    }

    public static Response patchWithAuth(String endpoint, Object body, String token) {
        return getRequestSpec().header("Authorization", "Bearer " + token).body(body).patch(endpoint);
    }

    // ---------- DELETE ----------

    public static Response delete(String endpoint) {
        return getRequestSpec().delete(endpoint);
    }

    public static Response deleteWithAuth(String endpoint, String token) {
        return getRequestSpec().header("Authorization", "Bearer " + token).delete(endpoint);
    }
}
