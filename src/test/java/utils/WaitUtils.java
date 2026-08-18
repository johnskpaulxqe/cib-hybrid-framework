package utils;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Custom wait helpers for conditions Playwright's built-in auto-wait doesn't
 * already cover (auto-wait handles visibility/actionability on click/fill/etc.
 * automatically — this class is for everything else: page-level state,
 * custom predicates, and polling on values that change asynchronously).
 */
public class WaitUtils {

    private static final int DEFAULT_TIMEOUT_MS = ConfigReader.getInt("defaultTimeoutMs", 30000);

    private WaitUtils() {
        // static-only utility class
    }

    /** Waits for full page load — DOM content + network idle. */
    public static void waitForPageLoad(Page page) {
        page.waitForLoadState(LoadState.LOAD);
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /** Waits for a specific element state (visible/hidden/attached/detached) beyond default auto-wait. */
    public static void waitForState(Locator locator, WaitForSelectorState state) {
        waitForState(locator, state, DEFAULT_TIMEOUT_MS);
    }

    public static void waitForState(Locator locator, WaitForSelectorState state, int timeoutMs) {
        locator.waitFor(new Locator.WaitForOptions().setState(state).setTimeout(timeoutMs));
    }

    /** Waits until the locator's text contains the expected substring — useful for async-rendered values. */
    public static void waitForTextContains(Locator locator, String expectedSubstring) {
        waitForTextContains(locator, expectedSubstring, DEFAULT_TIMEOUT_MS);
    }

    public static void waitForTextContains(Locator locator, String expectedSubstring, int timeoutMs) {
        fluentWaitForCondition(
                () -> locator.textContent() != null && locator.textContent().contains(expectedSubstring),
                timeoutMs,
                "Timed out waiting for text to contain [" + expectedSubstring + "], last seen: ["
                        + safeText(locator) + "]");
    }

    /** Waits for the page URL to contain a fragment — same intent as post-navigation checks in the Selenium version. */
    public static void waitForUrlContains(Page page, String fragment) {
        waitForUrlContains(page, fragment, DEFAULT_TIMEOUT_MS);
    }

    public static void waitForUrlContains(Page page, String fragment, int timeoutMs) {
        fluentWaitForCondition(
                () -> page.url().contains(fragment),
                timeoutMs,
                "Timed out waiting for URL to contain [" + fragment + "], last seen: [" + page.url() + "]");
    }

    /**
     * Generic polling wait for any custom boolean condition — the escape hatch
     * for banking-specific async UI states (e.g. a payment status flipping from
     * "Pending" to "Submitted" after a backend call resolves).
     */
    public static void fluentWaitForCondition(java.util.function.Supplier<Boolean> condition,
                                               int timeoutMs, String timeoutMessage) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        long pollIntervalMs = 500;
        while (System.currentTimeMillis() < deadline) {
            try {
                if (condition.get()) return;
            } catch (Exception ignored) {
                // condition not evaluable yet (e.g. element not attached) — keep polling
            }
            sleepQuietly(pollIntervalMs);
        }
        throw new RuntimeException(timeoutMessage);
    }

    public static void fluentWaitForCondition(java.util.function.Supplier<Boolean> condition, int timeoutMs) {
        fluentWaitForCondition(condition, timeoutMs, "Timed out waiting for custom condition after " + timeoutMs + "ms");
    }

    private static String safeText(Locator locator) {
        try {
            return locator.textContent();
        } catch (Exception e) {
            return "<unavailable>";
        }
    }

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
