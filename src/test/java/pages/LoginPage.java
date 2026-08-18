package pages;

import utils.ConfigReader;
import utils.Locators;
import utils.PageVerifier;
import utils.TestDataLoader;

public class LoginPage extends BasePage {

    /** Which sign-on flow is currently active, so clickLogin() hits the right submit button. */
    public enum LoginFlow { PNC, PINACLE }

    private LoginFlow activeFlow = LoginFlow.PNC;

    /** Navigates to the base URL and opens the sign-on flyout. Stops before choosing PNC vs PINACLE. */
    public void navigateToLogin() {
        navigateToUrl(ConfigReader.get("baseUrl"));
        click(Locators.Login.PNC_TOP_NAV_SIGN_ON);
    }

    /** Chooses "Log In to PNC" from the already-open sign-on flyout. */
    public void selectPncSignOn() {
        activeFlow = LoginFlow.PNC;
        click(Locators.Login.PNC_SIGN_ON_OPTION);
    }

    /** Chooses "Log In to PINACLE" from the already-open sign-on flyout. */
    public void selectPinacleSignOn() {
        activeFlow = LoginFlow.PINACLE;
        click(Locators.Login.PINACLE_SIGN_ON_OPTION);
    }

    public void enterCredentials(String username, String password) {
        waitForVisible(Locators.Login.PNC_USERNAME);
        type(Locators.Login.PNC_USERNAME, username);
        type(Locators.Login.PNC_PASSWORD, password);
    }

    /** PINACLE's form has three fields — Company ID, User ID, and Password — unlike PNC's two. */
    public void enterPinacleCredentials(String companyId, String userId, String password) {
        waitForVisible(Locators.Login.PINACLE_COMPANY_ID);
        type(Locators.Login.PINACLE_COMPANY_ID, companyId);
        type(Locators.Login.PINACLE_USER_ID, userId);
        type(Locators.Login.PINACLE_PASSWORD, password);
    }

    /** Clicks the submit button belonging to whichever flow was selected (PNC or PINACLE). */
    public void clickLogin() {
        if (activeFlow == LoginFlow.PINACLE) {
            click(Locators.Login.PINACLE_SUBMIT_BUTTON);
        } else {
            click(Locators.Login.PNC_SIGN_ON_BUTTON);
        }
    }

    /** Full login flow driven by a role key from users.json, e.g. loginAs("corporateAdmin"). */
    public void loginAs(String role) {
        navigateToLogin();
        selectPncSignOn();
        enterCredentials(TestDataLoader.getUsername(role), TestDataLoader.getPassword(role));
        clickLogin();
    }

    public boolean isMfaPromptDisplayed() {
        return isVisible(Locators.Login.MFA_CODE_INPUT);
    }

    public void submitMfaCode(String code) {
        type(Locators.Login.MFA_CODE_INPUT, code);
        click(Locators.Login.MFA_SUBMIT_BUTTON);
    }

    public void navigateToUrl(String url) {
        navigateTo(url);
    }

    public boolean isLinkVisible(String linkText) {
        // Try text-based selector first
        if (isVisible(Locators.linkWithText(linkText))) {
            return true;
        }

        // Fallback: some pages use dynamic IDs like 'button-<uuid>' — look for anchors
        // with id starting with 'button-' and compare their visible text.
        try {
            java.util.List<com.microsoft.playwright.Locator> anchors = page().locator("a[id^='button-']").all();
            for (com.microsoft.playwright.Locator a : anchors) {
                try {
                    String text = a.textContent();
                    if (text != null && text.trim().contains(linkText) && a.isVisible()) {
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    public boolean isCurrentUrl(String expectedUrl) {
        return getCurrentUrl().contains(expectedUrl);
    }

    public boolean isErrorDisplayed() {
        if (activeFlow == LoginFlow.PINACLE) {
            return isVisible(Locators.Login.PINACLE_ERROR_MESSAGE);
        }
        return isVisible(Locators.Login.ERROR_MESSAGE);
    }

    public String getErrorText() {
        if (activeFlow == LoginFlow.PINACLE) {
            return getText(Locators.Login.PINACLE_ERROR_MESSAGE);
        }
        return getText(Locators.Login.ERROR_MESSAGE);
    }

    public void verifyLoginSuccess() {
        PageVerifier.verifySuccessfulLogin(page());
    }

    public void verifyLoginFailure() {
        if (activeFlow == LoginFlow.PINACLE) {
            // PINACLE shows an inline error message rather than redirecting to a PNC-style
            // authenticate endpoint, so it can't reuse PageVerifier.verifyFailedLogin() as-is.
            // The banner renders only after the login POST resolves, so wait for it rather
            // than checking instantaneously.
            waitForVisible(Locators.Login.PINACLE_ERROR_MESSAGE);
        } else {
            PageVerifier.verifyFailedLogin(page());
        }
    }
}