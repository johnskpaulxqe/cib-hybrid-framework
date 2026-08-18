package stepdefinitions;

import context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import repository.PaymentRepository;
import utils.CustomAssert;
import utils.TestDataLoader;

public class DbSteps {

    private final TestContext ctx;
    private final PaymentRepository paymentRepository;

    public DbSteps(TestContext ctx) {
        this.ctx = ctx;
        this.paymentRepository = new PaymentRepository();
    }

    @Given("the database is reachable")
    public void theDatabaseIsReachable() {
        CustomAssert.assertTrue(utils.DBUtils.isConnected(), "DB connection state");
    }

    @Then("the payment should exist in the database")
    public void thePaymentShouldExistInTheDatabase() {
        String paymentId = ctx.getCreatedPaymentId();
        CustomAssert.assertNotNull(paymentId, "Payment ID from prior step");
        CustomAssert.assertTrue(paymentRepository.paymentExistsById(paymentId), "Payment existence in DB");
    }

    @Then("the payment status in the database should be {string}")
    public void thePaymentStatusInTheDatabaseShouldBe(String expectedStatus) {
        String actualStatus = paymentRepository.getPaymentStatus(ctx.getCreatedPaymentId());
        CustomAssert.assertEquals(actualStatus, expectedStatus, "Payment status in DB");
    }

    @When("I query pending payments for the logged in entity")
    public void iQueryPendingPaymentsForTheLoggedInEntity() {
        String entityId = TestDataLoader.getEntityId(ctx.getLoggedInUsername());
        int count = paymentRepository.countPendingPaymentsForEntity(entityId);
        ctx.put("pendingPaymentCount", count);
    }

    @Then("the pending payment count should be greater than {int}")
    public void thePendingPaymentCountShouldBeGreaterThan(int minCount) {
        int actual = (int) ctx.get("pendingPaymentCount");
        CustomAssert.assertTrue(actual > minCount, "Pending payment count");
    }

    @Then("I clean up the test payment")
    public void iCleanUpTheTestPayment() {
        String paymentId = ctx.getCreatedPaymentId();
        if (paymentId != null) {
            paymentRepository.deleteTestPayment(paymentId);
        }
    }
}
