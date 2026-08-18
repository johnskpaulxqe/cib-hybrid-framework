package stepdefinitions;

import context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import utils.ApiUtils;
import utils.ResponseValidator;
import utils.TestDataLoader;

public class ApiSteps {

    private final TestContext ctx;

    public ApiSteps(TestContext ctx) {
        this.ctx = ctx;
    }

    @Given("I authenticate via the API as {string}")
    public void iAuthenticateViaTheApiAs(String role) {
        String endpoint = TestDataLoader.getApiEndpoint("login");
        Response response = ApiUtils.post(endpoint, TestDataLoader.getRequestBody("login"));
        ResponseValidator.assertStatusCode(response, 200);

        String token = ResponseValidator.extractString(response, "token");
        ctx.setAuthToken(token);
        ctx.setLoggedInUsername(role);
    }

    @When("I initiate a wire transfer via API")
    public void iInitiateAWireTransferViaApi() {
        String endpoint = TestDataLoader.getApiEndpoint("paymentInitiation");
        Response response = ApiUtils.postWithAuth(
                endpoint, TestDataLoader.getRequestBody("initiateWireTransfer"), ctx.getAuthToken());
        ctx.setLastApiResponse(response);

        if (response.getStatusCode() == 201) {
            String paymentId = ResponseValidator.extractString(response, "paymentId");
            ctx.setCreatedPaymentId(paymentId);
        }
    }

    @When("I create an FX deal via API")
    public void iCreateAnFxDealViaApi() {
        String endpoint = TestDataLoader.getApiEndpoint("fxDeals");
        Response response = ApiUtils.postWithAuth(
                endpoint, TestDataLoader.getRequestBody("createFxDeal"), ctx.getAuthToken());
        ctx.setLastApiResponse(response);

        if (response.getStatusCode() == 201) {
            ctx.setCreatedFxDealReference(ResponseValidator.extractString(response, "dealReference"));
        }
    }

    @When("I check the payment status via API")
    public void iCheckThePaymentStatusViaApi() {
        Response response = utils.RequestBuilder.create()
                .withAuth(ctx.getAuthToken())
                .withPathParam("paymentId", ctx.getCreatedPaymentId())
                .get("/payments/{paymentId}/status");
        ctx.setLastApiResponse(response);
    }

    @Then("the API response status code should be {int}")
    public void theApiResponseStatusCodeShouldBe(int expectedCode) {
        ResponseValidator.assertStatusCode(ctx.getLastApiResponse(), expectedCode);
    }

    @Then("the API response field {string} should equal {string}")
    public void theApiResponseFieldShouldEqual(String jsonPath, String expectedValue) {
        ResponseValidator.assertFieldEquals(ctx.getLastApiResponse(), jsonPath, expectedValue);
    }

    @Then("the API response field {string} should not be null")
    public void theApiResponseFieldShouldNotBeNull(String jsonPath) {
        ResponseValidator.assertFieldNotNull(ctx.getLastApiResponse(), jsonPath);
    }

    @Then("the API response time should be within SLA")
    public void theApiResponseTimeShouldBeWithinSla() {
        ResponseValidator.assertResponseTimeWithinSla(ctx.getLastApiResponse());
    }
}
