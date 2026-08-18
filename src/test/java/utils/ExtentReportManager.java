package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

/**
 * Utility wrapper around Extent Reports. Step-level detail (which Gherkin
 * step ran, pass/fail, screenshots) is owned by the
 * extentreports-cucumber7-adapter Cucumber plugin — declared on each runner
 * in Phase 8 — NOT by manual calls from Hooks.java. That plugin listens to
 * Cucumber's own step lifecycle directly, which is the only clean way to get
 * step names into the report (Cucumber doesn't expose the current step name
 * to @AfterStep).
 *
 * This class exists as a safety net: manual test/log methods for edge cases,
 * plus flushReport() so the report is written to disk even when a run
 * bypasses testng.xml (e.g. launched from an IDE class runner).
 */
public class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    private ExtentReportManager() {
        // static-only utility class
    }

    private static synchronized void createReport() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("target/extent-reports/report.html");
            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Environment", ConfigReader.getActiveEnv());
            extent.setSystemInfo("Browser", System.getProperty("browser", ConfigReader.get("browser", "chromium")));
            LogUtil.info(ExtentReportManager.class, "Extent report initialised at target/extent-reports/report.html");
        }
    }

    /** Manual test creation — rarely needed since the Cucumber adapter creates nodes automatically. Kept for edge cases. */
    public static void createTest(String name, String tags) {
        createReport();
        ExtentTest test = extent.createTest(name).assignCategory(tags);
        testThreadLocal.set(test);
    }

    public static ExtentTest getTest() {
        return testThreadLocal.get();
    }

    public static void logPass(String message) {
        if (getTest() != null) getTest().pass(message);
    }

    public static void logFail(String message) {
        if (getTest() != null) getTest().fail(message);
    }

    public static void logInfo(String message) {
        if (getTest() != null) getTest().info(message);
    }

    /** Safety net — flush is normally handled by the adapter, but calling this after Hooks is harmless and cheap. */
    public static synchronized void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }
}
