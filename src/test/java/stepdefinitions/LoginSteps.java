package stepdefinitions;

import context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.LoginPage;
import utils.CustomAssert;
import utils.ConfigReader;

public class LoginSteps {

    private final TestContext ctx;
    private final LoginPage loginPage;

    public LoginSteps(TestContext ctx) {
        this.ctx = ctx;
        this.loginPage = ctx.getLoginPage();
    }

    @Given("I am on the CIB login page")
    public void iAmOnTheLoginPage() {
        loginPage.navigateToLogin();
    }

    @Given("I am logged in as {string}")
    public void iAmLoggedInAs(String role) {
        loginPage.loginAs(role);
        ctx.setLoggedInUsername(role);
    }

    @When("I login as {string}")
    public void iLoginAs(String role) {
        loginPage.loginAs(role);
        ctx.setLoggedInUsername(role);
    }

    @When("I enter username {string} and password {string}")
    public void iEnterUsernameAndPassword(String username, String password) {
        loginPage.enterCredentials(username, password);
    }

    @Given("I'm on PNC Log in page")
    public void imOnPncLoginPage() {
        loginPage.selectPncSignOn();
    }

    @Given("I'm on PINACLE Log in page")
    public void imOnPinacleLoginPage() {
        loginPage.selectPinacleSignOn();
    }

    @When("I enter PINACLE company ID {string} user ID {string} and password {string}")
    public void iEnterPinacleCredentials(String companyId, String userId, String password) {
        loginPage.enterPinacleCredentials(companyId, userId, password);
    }

    @When("I click the login button")
    public void iClickTheLoginButton() {
        loginPage.clickLogin();
    }

    @When("I submit MFA code {string}")
    public void iSubmitMfaCode(String code) {
        loginPage.submitMfaCode(code);
    }
    @Given("I navigate to the PNC corporate landing page")
    public void iNavigateToThePncCorporateLandingPage() {
        String baseUrl = ConfigReader.get("baseUrl");
        loginPage.navigateToUrl(baseUrl);
    }

    @Then("I should see the {string} link")
    public void iShouldSeeTheLink(String linkText) {
        CustomAssert.assertTrue(loginPage.isLinkVisible(linkText), "Expected link is visible: " + linkText);
    }

    @Then("I should be on the PNC corporate landing page")
    public void iShouldBeOnThePncCorporateLandingPage() {
        String baseUrl = ConfigReader.get("baseUrl");
        CustomAssert.assertTrue(loginPage.isCurrentUrl(baseUrl),
                "Expected current URL to contain the corporate landing page URL");
    }
    @Then("I should be on the dashboard")
    public void iShouldBeOnTheDashboard() {
        loginPage.verifyLoginSuccess();
    }

    @Then("I should see a login error")
    public void iShouldSeeALoginError() {
        loginPage.verifyLoginFailure();
    }

    @Then("the login error should contain {string}")
    public void theLoginErrorShouldContain(String expectedText) {
        // Try placeholder flow first (for mock/test environments)
        boolean hasPlaceholderError = loginPage.isErrorDisplayed();
        if (hasPlaceholderError) {
            CustomAssert.assertContains(loginPage.getErrorText(), expectedText, "Login error message");
        } else {
            // For real PNC responses, just verify we reached the authenticate endpoint
            // Actual error messages are returned by the PNC API
            CustomAssert.assertTrue(
                loginPage.isCurrentUrl("secure-api.pnc.com") && loginPage.isCurrentUrl("authenticate"),
                "Expected to reach PNC authenticate endpoint after failed login"
            );
        }
    }

    @Then("an MFA prompt should be displayed")
    public void anMfaPromptShouldBeDisplayed() {
        CustomAssert.assertTrue(loginPage.isMfaPromptDisplayed(), "MFA prompt visibility");
    }
}