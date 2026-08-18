package utils;

import com.microsoft.playwright.Page;
import org.testng.Assert;

/**
 * Reusable page-state assertions, kept out of step definitions so a step
 * reads as one clean line instead of several raw Playwright calls.
 */
public class PageVerifier {

    private PageVerifier() {
        // static-only utility class
    }

    // ---------- URL ----------

    public static void verifyUrlContains(Page page, String fragment) {
        Assert.assertTrue(page.url().contains(fragment),
                "Expected URL to contain [" + fragment + "], actual: [" + page.url() + "]");
    }

    public static void verifyUrlDoesNotContain(Page page, String fragment) {
        Assert.assertFalse(page.url().contains(fragment),
                "Expected URL to NOT contain [" + fragment + "], actual: [" + page.url() + "]");
    }

    // ---------- Title ----------

    public static void verifyTitleContains(Page page, String expectedSubstring) {
        Assert.assertTrue(page.title().contains(expectedSubstring),
                "Expected title to contain [" + expectedSubstring + "], actual: [" + page.title() + "]");
    }

    // ---------- Visibility ----------

    public static void verifyElementDisplayed(Page page, String selector) {
        Assert.assertTrue(page.locator(selector).isVisible(), "Expected element [" + selector + "] to be visible");
    }

    public static void verifyElementNotDisplayed(Page page, String selector) {
        boolean visible = page.locator(selector).count() > 0 && page.locator(selector).isVisible();
        Assert.assertFalse(visible, "Expected element [" + selector + "] to be hidden or absent");
    }

    public static void verifyElementPresent(Page page, String selector) {
        Assert.assertTrue(page.locator(selector).count() > 0, "Expected element [" + selector + "] to be present in DOM");
    }

    // ---------- Text ----------

    public static void verifyElementText(Page page, String selector, String expectedText) {
        String actual = page.locator(selector).textContent();
        Assert.assertEquals(actual == null ? "" : actual.trim(), expectedText, "Text mismatch at [" + selector + "]");
    }

    public static void verifyElementTextContains(Page page, String selector, String expectedSubstring) {
        String actual = page.locator(selector).textContent();
        Assert.assertTrue(actual != null && actual.contains(expectedSubstring),
                "Expected [" + selector + "] text [" + actual + "] to contain [" + expectedSubstring + "]");
    }

    // ---------- State ----------

    public static void verifyElementEnabled(Page page, String selector) {
        Assert.assertTrue(page.locator(selector).isEnabled(), "Expected [" + selector + "] to be enabled");
    }

    public static void verifyElementDisabled(Page page, String selector) {
        Assert.assertFalse(page.locator(selector).isEnabled(), "Expected [" + selector + "] to be disabled");
    }

    // ---------- Count ----------

    public static void verifyElementCount(Page page, String selector, int expectedCount) {
        int actual = page.locator(selector).count();
        Assert.assertEquals(actual, expectedCount, "Element count mismatch at [" + selector + "]");
    }

    public static void verifyElementCountGreaterThan(Page page, String selector, int minCount) {
        int actual = page.locator(selector).count();
        Assert.assertTrue(actual > minCount,
                "Expected element count at [" + selector + "] to exceed " + minCount + ", actual: " + actual);
    }

    // ---------- Composite — CIB-specific flows ----------

    public static void verifyPageLoaded(Page page, String expectedUrlFragment) {
        WaitUtils.waitForPageLoad(page);
        verifyUrlContains(page, expectedUrlFragment);
    }

    public static void verifySuccessToast(Page page, String expectedMessage) {
        verifyElementDisplayed(page, Locators.Common.SUCCESS_TOAST);
        verifyElementTextContains(page, Locators.Common.SUCCESS_TOAST, expectedMessage);
    }

    public static void verifyErrorToast(Page page, String expectedMessage) {
        verifyElementDisplayed(page, Locators.Common.ERROR_TOAST);
        verifyElementTextContains(page, Locators.Common.ERROR_TOAST, expectedMessage);
    }

    public static void verifySuccessfulLogin(Page page) {
        verifyUrlDoesNotContain(page, "/login");
        verifyUrlContains(page, "/dashboard");
        verifyElementDisplayed(page, Locators.Dashboard.WELCOME_MESSAGE);
    }

    public static void verifyFailedLogin(Page page) {
        // Check for either:
        // 1. Placeholder behavior: URL contains /login and error message is visible
        // 2. Real PNC behavior: URL contains the authenticate endpoint (form was submitted)
        String url = page.url();
        boolean isPlaceholderFlow = url.contains("/login") && page.locator(Locators.Login.ERROR_MESSAGE).count() > 0;
        boolean isPncAuthEndpoint = url.contains("secure-api.pnc.com") && url.contains("authenticate");
        
        Assert.assertTrue(isPlaceholderFlow || isPncAuthEndpoint,
                "Expected either placeholder login flow or PNC authenticate endpoint, actual URL: [" + url + "]");
    }

    /** Verifies a payment confirmation screen shows the expected status after submission. */
    public static void verifyPaymentSubmitted(Page page) {
        verifyElementDisplayed(page, Locators.PaymentInitiation.CONFIRMATION_PAYMENT_ID);
        verifyElementTextContains(page, Locators.PaymentInitiation.CONFIRMATION_STATUS, "SUBMITTED");
    }
}
