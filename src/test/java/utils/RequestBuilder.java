package utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Chainable, typed request builder for cases ApiUtils' one-liners don't cover:
 * path params (/payments/{id}/status), multiple custom headers, form data,
 * or a reusable request template you want to log for debugging.
 */
public class RequestBuilder {

    private final RequestSpecification spec;

    private RequestBuilder() {
        this.spec = RestAssured.given()
                .contentType("application/json")
                .accept("application/json");
    }

    public static RequestBuilder create() {
        return new RequestBuilder();
    }

    public RequestBuilder withAuth(String token) {
        spec.header("Authorization", "Bearer " + token);
        return this;
    }

    public RequestBuilder withHeader(String name, String value) {
        spec.header(name, value);
        return this;
    }

    public RequestBuilder withBody(Object body) {
        spec.body(body);
        return this;
    }

    public RequestBuilder withPathParam(String name, Object value) {
        spec.pathParam(name, value);
        return this;
    }

    public RequestBuilder withQueryParam(String name, Object value) {
        spec.queryParam(name, value);
        return this;
    }

    public RequestBuilder withFormParam(String name, Object value) {
        spec.formParam(name, value);
        return this;
    }

    public RequestBuilder withLogging() {
        spec.log().all();
        return this;
    }

    public Response get(String endpoint) {
        return spec.get(endpoint);
    }

    public Response post(String endpoint) {
        return spec.post(endpoint);
    }

    public Response put(String endpoint) {
        return spec.put(endpoint);
    }

    public Response patch(String endpoint) {
        return spec.patch(endpoint);
    }

    public Response delete(String endpoint) {
        return spec.delete(endpoint);
    }
}
