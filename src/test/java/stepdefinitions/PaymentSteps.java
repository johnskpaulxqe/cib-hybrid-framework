package stepdefinitions;

import context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.PaymentInitiationPage;
import utils.CustomAssert;

public class PaymentSteps {

    private final TestContext ctx;
    private final PaymentInitiationPage paymentPage;

    public PaymentSteps(TestContext ctx) {
        this.ctx = ctx;
        this.paymentPage = ctx.getPaymentInitiationPage();
    }

    @Given("I am on the payment initiation page")
    public void iAmOnThePaymentInitiationPage() {
        paymentPage.navigateToPaymentInitiation();
    }

    @When("I initiate a wire transfer from {string} to account {string} for {string} {string} with reference {string}")
    public void iInitiateAWireTransfer(String fromAccount, String toAccount, String amount,
                                        String currency, String reference) {
        paymentPage.initiateWireTransfer(fromAccount, toAccount, amount, currency, reference);
    }

    @When("I submit the payment")
    public void iSubmitThePayment() {
        paymentPage.clickSubmit();
    }

    @Then("the payment should be submitted successfully")
    public void thePaymentShouldBeSubmittedSuccessfully() {
        paymentPage.verifySubmissionSuccess();
        String paymentId = paymentPage.getConfirmedPaymentId();
        ctx.setCreatedPaymentId(paymentId);
        ctx.put("lastConfirmedPaymentId", paymentId);
    }

    @Then("the confirmation status should be {string}")
    public void theConfirmationStatusShouldBe(String expectedStatus) {
        CustomAssert.assertContains(paymentPage.getConfirmedStatus(), expectedStatus, "Payment confirmation status");
    }

    @Then("a payment ID should be generated")
    public void aPaymentIdShouldBeGenerated() {
        CustomAssert.assertNotNull(ctx.getCreatedPaymentId(), "Generated payment ID");
    }
}
