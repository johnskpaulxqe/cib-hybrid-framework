package utils;

import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.util.regex.Pattern;

/**
 * Wraps TestNG hard and soft assertions with consistent logging.
 * Hard assertions stop the scenario immediately; soft assertions collect
 * every failure so a step (e.g. verifying every field on a payment
 * confirmation screen) can report all mismatches in one go.
 */
public class CustomAssert {

    private static final ThreadLocal<SoftAssert> softAssertThreadLocal = ThreadLocal.withInitial(SoftAssert::new);

    private CustomAssert() {
        // static-only utility class
    }

    // ---------- Hard assertions ----------

    public static void assertEquals(Object actual, Object expected, String context) {
        LogUtil.info(CustomAssert.class, "Assert [{}]: expected=[{}] actual=[{}]", context, expected, actual);
        Assert.assertEquals(actual, expected, context);
    }

    public static void assertNotEquals(Object actual, Object unexpected, String context) {
        Assert.assertNotEquals(actual, unexpected, context);
    }

    public static void assertTrue(boolean condition, String context) {
        Assert.assertTrue(condition, context);
    }

    public static void assertFalse(boolean condition, String context) {
        Assert.assertFalse(condition, context);
    }

    public static void assertNull(Object actual, String context) {
        Assert.assertNull(actual, context);
    }

    public static void assertNotNull(Object actual, String context) {
        Assert.assertNotNull(actual, context);
    }

    public static void assertContains(String actual, String expectedSubstring, String context) {
        Assert.assertTrue(actual != null && actual.contains(expectedSubstring),
                context + " — expected [" + actual + "] to contain [" + expectedSubstring + "]");
    }

    public static void assertContainsIgnoreCase(String actual, String expectedSubstring, String context) {
        Assert.assertTrue(actual != null && actual.toLowerCase().contains(expectedSubstring.toLowerCase()),
                context + " — expected [" + actual + "] to contain (ignoring case) [" + expectedSubstring + "]");
    }

    public static void assertMatches(String actual, String regex, String context) {
        Assert.assertTrue(actual != null && Pattern.matches(regex, actual),
                context + " — expected [" + actual + "] to match regex [" + regex + "]");
    }

    public static void assertGreaterThan(double actual, double threshold, String context) {
        Assert.assertTrue(actual > threshold,
                context + " — expected [" + actual + "] to be greater than [" + threshold + "]");
    }

    public static void assertStatusCode(int actual, int expected, String context) {
        Assert.assertEquals(actual, expected, context + " — HTTP status mismatch");
    }

    // ---------- Soft assertions ----------

    public static void softAssertEquals(Object actual, Object expected, String context) {
        LogUtil.info(CustomAssert.class, "Soft assert [{}]: expected=[{}] actual=[{}]", context, expected, actual);
        softAssertThreadLocal.get().assertEquals(actual, expected, context);
    }

    public static void softAssertTrue(boolean condition, String context) {
        softAssertThreadLocal.get().assertTrue(condition, context);
    }

    public static void softAssertContains(String actual, String expectedSubstring, String context) {
        boolean result = actual != null && actual.contains(expectedSubstring);
        softAssertThreadLocal.get().assertTrue(result,
                context + " — expected [" + actual + "] to contain [" + expectedSubstring + "]");
    }

    /** Flushes all collected soft-assertion failures for this thread. Call at the end of a multi-field verification step. */
    public static void assertAll(String context) {
        try {
            softAssertThreadLocal.get().assertAll(context);
        } finally {
            resetSoftAssert();
        }
    }

    /** Safety reset — call from Hooks.java @After so a failed soft assert never bleeds into the next scenario. */
    public static void resetSoftAssert() {
        softAssertThreadLocal.remove();
    }
}
